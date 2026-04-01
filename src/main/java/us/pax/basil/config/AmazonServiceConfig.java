package us.pax.basil.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import us.pax.basil.service.aws.ses.DefaultEmailService;
import us.pax.basil.service.aws.ses.EmailService;
import us.pax.basil.service.aws.ses.EmailTemplateService;
import us.pax.basil.service.aws.ses.SimpleEmailService;
import us.pax.basil.service.aws.ses.SmtpEmailService;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class AmazonServiceConfig {

    private final EmailTemplateService emailTemplateService;

    private final Executor awsExecutor;

    @Value("${aws.region:us-east-1}")
    private String region;

    @Value("${aws.enabled:false}")
    private boolean enabled;

    @Value("${aws.local_development:false}")
    private boolean localDevelopment;

    @Value("${aws.accessKeyId:null}")
    private String accessKeyId;

    @Value("${aws.secretKey:null}")
    private String secretKey;

    @Value("${aws.sessionToken:null}")
    private String sessionToken;

    @Value("${mail.smtp.enabled:false}")
    private boolean smtpEnabled;

    @Value("${mail.smtp.host:}")
    private String smtpHost;

    @Value("${mail.smtp.port:25}")
    private Integer smtpPort;

    @Value("${mail.smtp.username:}")
    private String smtpUsername;

    @Value("${mail.smtp.password:}")
    private String smtpPassword;

    @Value("${mail.smtp.from:}")
    private String smtpFrom;

    @Value("${mail.smtp.auth:true}")
    private boolean smtpAuth;

    @Value("${mail.smtp.starttls:true}")
    private boolean smtpStarttls;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        try {
            if (!accessKeyId.isEmpty() && !secretKey.isEmpty() && !sessionToken.isEmpty() && localDevelopment) {
                // Using temporary credentials
                return StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKeyId, secretKey, sessionToken));
            } else {
                // Using EC2 instance default credentials
                return DefaultCredentialsProvider.create();
            }
        } catch (Exception e) {
            log.error("Failed to create AWS credentials provider. Fallback service may be used.", e);
            return null;
        }
    }

    @Bean
    public EmailService emailService() {
        if (smtpEnabled) {
            log.info("Email service initialized with SMTP provider (non-SES mode).");
            return new SmtpEmailService(
                    awsExecutor,
                    emailTemplateService,
                    smtpHost,
                    smtpPort,
                    smtpUsername,
                    smtpPassword,
                    smtpFrom,
                    smtpAuth,
                    smtpStarttls
            );
        }

        AwsCredentialsProvider awsCredentialsProvider = awsCredentialsProvider();
        if (awsCredentialsProvider != null && enabled) {
            log.info("Email service initialized with AWS SES provider.");
            return new SimpleEmailService(awsCredentialsProvider, awsExecutor, emailTemplateService, region);
        } else {
            log.warn("Email service initialized with DefaultEmailService (no-op).");
            return new DefaultEmailService();
        }
    }
}
