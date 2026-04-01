package us.pax.basil.service.aws.ses;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.core.exception.SdkException;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * SMTP 邮件服务（用于 dev 环境）。
 *
 * 设计说明：
 * 1. dev 环境不走 AWS SES，改为标准 SMTP 用户名/密码方式发送邮件。
 * 2. 保持与 EmailService 接口一致，业务层无需感知底层发送通道变化。
 * 3. 支持 HTML 正文 + 可选附件，满足 Contact RMA 截图随邮件发送的需求。
 */
@Log4j2
public class SmtpEmailService implements EmailService {

    private final Executor asyncExecutor;

    private final EmailTemplateService templateService;

    private final String smtpHost;

    private final Integer smtpPort;

    private final String smtpUsername;

    private final String smtpPassword;

    private final String fromAddress;

    private final boolean smtpAuth;

    private final boolean startTlsEnabled;

    public SmtpEmailService(Executor asyncExecutor,
                            EmailTemplateService templateService,
                            String smtpHost,
                            Integer smtpPort,
                            String smtpUsername,
                            String smtpPassword,
                            String fromAddress,
                            boolean smtpAuth,
                            boolean startTlsEnabled) {
        this.asyncExecutor = asyncExecutor;
        this.templateService = templateService;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort == null ? 25 : smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.fromAddress = fromAddress;
        this.smtpAuth = smtpAuth;
        this.startTlsEnabled = startTlsEnabled;
    }

    @Override
    public CompletableFuture<SESResponse> sendEmail(String to, String subject, String htmlBody) {
        return CompletableFuture.supplyAsync(
                () -> sendEmailWithAttachmentSync(to, subject, htmlBody, null, null, null),
                asyncExecutor
        );
    }

    @Override
    public CompletableFuture<SESResponse> sendEmailWithAttachment(String to,
                                                                  String subject,
                                                                  String htmlBody,
                                                                  String attachmentFileName,
                                                                  String attachmentContentType,
                                                                  byte[] attachmentBytes) {
        return CompletableFuture.supplyAsync(
                () -> sendEmailWithAttachmentSync(
                        to,
                        subject,
                        htmlBody,
                        attachmentFileName,
                        attachmentContentType,
                        attachmentBytes
                ),
                asyncExecutor
        );
    }

    @Override
    public CompletableFuture<List<SESResponse>> sendEmails(List<String> recipients, String subject, String htmlBody) {
        return CompletableFuture.supplyAsync(() -> {
            List<SESResponse> responses = new ArrayList<>();
            for (String recipient : recipients) {
                responses.add(sendEmailWithAttachmentSync(recipient, subject, htmlBody, null, null, null));
            }
            return responses;
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<List<SESResponse>> sendTemplatedEmail(String subject, String templateName, Map<String, Object> model, String... recipients) {
        String htmlBody = templateService.build(templateName, model);
        if (recipients == null || recipients.length < 1) {
            throw new IllegalArgumentException("Email must contain at least one recipient");
        }
        return sendEmails(Arrays.asList(recipients), subject, htmlBody);
    }

    /**
     * 发送邮件核心实现（支持附件）。
     *
     * @param to 收件人
     * @param subject 邮件主题
     * @param htmlBody HTML 内容
     * @param attachmentFileName 附件文件名（可空）
     * @param attachmentContentType 附件 MIME 类型（可空）
     * @param attachmentBytes 附件二进制（可空）
     * @return 统一邮件发送结果
     */
    private SESResponse sendEmailWithAttachmentSync(String to,
                                                    String subject,
                                                    String htmlBody,
                                                    String attachmentFileName,
                                                    String attachmentContentType,
                                                    byte[] attachmentBytes) {
        try {
            Session session = buildMailSession();
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(resolveFromAddress()));
            mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(subject, "UTF-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");

            MimeMultipart mixedMultipart = new MimeMultipart("mixed");
            mixedMultipart.addBodyPart(htmlPart);

            if (attachmentBytes != null
                    && attachmentBytes.length > 0
                    && attachmentFileName != null
                    && attachmentFileName.trim().length() > 0) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                String contentType = (attachmentContentType == null || attachmentContentType.trim().isEmpty())
                        ? "application/octet-stream"
                        : attachmentContentType;
                ByteArrayDataSource dataSource = new ByteArrayDataSource(attachmentBytes, contentType);
                attachmentPart.setDataHandler(new DataHandler(dataSource));
                attachmentPart.setFileName(attachmentFileName);
                mixedMultipart.addBodyPart(attachmentPart);
            }

            mimeMessage.setContent(mixedMultipart);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);

            log.info(
                    "SMTP email sent successfully. subject={}, to={}, messageId={}",
                    subject,
                    to,
                    mimeMessage.getMessageID()
            );

            return SESResponse.builder()
                    .success(true)
                    .messageId(mimeMessage.getMessageID())
                    .build();
        } catch (Exception e) {
            log.error("Failed to send SMTP email. subject={}, to={}", subject, to, e);
            return SESResponse.builder()
                    .success(false)
                    .exception(SdkException.builder().message(e.getMessage()).build())
                    .build();
        }
    }

    /**
     * 构建 SMTP 会话。
     * 说明：
     * 1. 当 smtpAuth=true 时使用用户名/密码鉴权。
     * 2. startTlsEnabled 由配置控制，便于 dev 环境快速切换。
     */
    private Session buildMailSession() {
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", String.valueOf(smtpPort));
        properties.put("mail.smtp.auth", String.valueOf(smtpAuth));
        properties.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnabled));
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        if (!smtpAuth) {
            return Session.getInstance(properties);
        }

        final String user = smtpUsername;
        final String pass = smtpPassword;
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });
    }

    /**
     * 解析发件人地址。
     * 规则：
     * 1. 优先使用配置中的 fromAddress；
     * 2. 为空时回退为 smtpUsername；
     * 3. 再为空则回退为 noreply@localhost，避免出现空发件人导致发送失败。
     */
    private String resolveFromAddress() {
        if (fromAddress != null && fromAddress.trim().length() > 0) {
            return fromAddress.trim();
        }
        if (smtpUsername != null && smtpUsername.trim().length() > 0) {
            return smtpUsername.trim();
        }
        return "noreply@localhost";
    }
}
