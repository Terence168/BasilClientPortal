package us.pax.basil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import us.pax.basil.service.aws.ses.DummyEmailService;
import us.pax.basil.service.aws.ses.EmailService;
import us.pax.basil.service.aws.ses.SimpleEmailService;
//import us.pax.basil.service.aws.ses.DummyEmailService;
//import us.pax.basil.service.aws.ses.EmailService;
//import us.pax.basil.service.aws.ses.SimpleEmailService;

@Configuration
public class AWSConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.enabled}")
    private boolean enabled;

    @Value("${aws.local_development}")
    private boolean localDevelopment;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(@Value("${aws.accessKeyId:}") String accessKeyId,
                                                         @Value("${aws.secretKey:}") String secretKey,
                                                         @Value("${aws.sessionToken:}") String sessionToken) {
        try {
            if (!accessKeyId.isEmpty() && !secretKey.isEmpty() && !sessionToken.isEmpty() && localDevelopment) {
                // Using temporary credentials
                return StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKeyId, secretKey, sessionToken));
            } else {
                // Using EC2 instance default credentials
                return DefaultCredentialsProvider.create();
            }
        } catch (Exception e) {
            System.err.println("Failed to create AWS credentials provider. Using dummy email service.");
            return null;
        }
    }

    @Bean
    public EmailService emailService(AwsCredentialsProvider awsCredentialsProvider) {
        if (awsCredentialsProvider != null && enabled) {
            return new SimpleEmailService(awsCredentialsProvider, region);
        } else {
            return new DummyEmailService();
        }
    }
}
