package us.pax.basil.service.aws.ses;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Log4j2
@ConditionalOnProperty(name = "aws.enabled", havingValue = "true")
public class SimpleEmailService implements EmailService {

    private final SesClient sesClient;

    private final Executor awsExecutor;

    private final EmailTemplateService templateService;

    @Autowired
    public SimpleEmailService(AwsCredentialsProvider credentialsProvider, @Qualifier("awsExecutor") Executor executor, EmailTemplateService templateService, @Value("${aws.region}") String region) {
        this.sesClient = SesClient.builder().region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
        this.awsExecutor = executor;
        this.templateService = templateService;
    }

    @Override
    public CompletableFuture<SESResponse> sendEmail(String to, String subject, String htmlBody) {
        return CompletableFuture.supplyAsync(() -> sendEmailSync(to, subject, htmlBody), awsExecutor);
    }

    @Override
    public CompletableFuture<List<SESResponse>> sendEmails(List<String> recipients, String subject, String htmlBody) {
        return CompletableFuture.supplyAsync(() -> {
            List<SESResponse> responses = new ArrayList<>();
            for (String recipient : recipients) {
                try {
                    SESResponse response = sendEmailSync(recipient, subject, htmlBody);
                    responses.add(response);
                } catch (SdkException e) {
                    log.error("Error sending email to " + recipient + ": " + e.getMessage(), e);
                    responses.add(SESResponse.builder()
                            .success(false)
                            .exception(e)
                            .build()
                    );
                } catch (Exception e) {
                    log.error("[Uncaught] Error sending email to " + recipient + ": " + e.getMessage(), e);
                    responses.add(SESResponse.builder()
                            .success(false)
                            .exception(SdkException.builder().message(e.getMessage()).build())
                            .build()
                    );
                }
            }
            return responses;
        }, awsExecutor);
    }

    @Override
    public CompletableFuture<List<SESResponse>> sendTemplatedEmail(String subject, String templateName, Map<String, Object> model, String... recipients) {
        String htmlBody = templateService.build(templateName, model);
        if (recipients == null || recipients.length < 1) {
            throw new IllegalArgumentException("Email must contain at least one recipient");
        } else {
            return sendEmails(Arrays.asList(recipients), subject, htmlBody);
        }
    }


    private SESResponse sendEmailSync(String to, String subject, String htmlBody) {
        try {
            Body body = Body.builder()
                    .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                    .build();

            Content subjectContent = Content.builder().data(subject).build();

            Message message = Message.builder()
                    .subject(subjectContent)
                    .body(body)
                    .build();

            Destination destination = Destination.builder()
                    .toAddresses(to)
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(destination)
                    .message(message)
                    .source("noreply-basil@pax.us")
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            return SESResponse.builder()
                    .success(true)
                    .response(response)
                    .build();
        } catch (SdkException e) {
            log.error("Failed to send email: {} to: {} | ERR: {}", subject, to, e.getMessage());
            return SESResponse.builder()
                    .success(false)
                    .exception(e)
                    .build();
        } catch (Exception e) {
            log.error("[Uncaught] Failed to send email: " + e.getMessage(), e);
            return SESResponse.builder()
                    .success(false)
                    .exception(SdkException.builder().message(e.getMessage()).build())
                    .build();
        }
    }
}
