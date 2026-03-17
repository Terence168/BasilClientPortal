package us.pax.basil.service.aws.ses;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface EmailService {

    CompletableFuture<SESResponse> sendEmail(String to, String subject, String htmlBody);

    /**
     * 发送带附件的邮件。
     *
     * @param to 收件人
     * @param subject 邮件主题
     * @param htmlBody HTML 邮件正文
     * @param attachmentFileName 附件文件名（可空）
     * @param attachmentContentType 附件 MIME 类型（可空）
     * @param attachmentBytes 附件二进制（可空）
     * @return SES 发送结果
     */
    CompletableFuture<SESResponse> sendEmailWithAttachment(String to,
                                                           String subject,
                                                           String htmlBody,
                                                           String attachmentFileName,
                                                           String attachmentContentType,
                                                           byte[] attachmentBytes);

    CompletableFuture<List<SESResponse>> sendEmails(List<String> recipients, String subject, String htmlBody);

    CompletableFuture<List<SESResponse>> sendTemplatedEmail(String subject, String templateName, Map<String, Object> model, String... recipients);
}
