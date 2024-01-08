package us.pax.basil.config;

import lombok.RequiredArgsConstructor;
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

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
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
            System.err.println("Failed to create AWS credentials provider. Using default email service.");
            return null;
        }
    }

    @Bean
    public EmailService emailService(AwsCredentialsProvider awsCredentialsProvider) {
        if (awsCredentialsProvider != null && enabled) {
            return new SimpleEmailService(awsCredentialsProvider, awsExecutor, emailTemplateService, region);
        } else {
            return new DefaultEmailService();
        }
    }
}