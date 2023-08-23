package us.pax.basil.service.aws.ses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;

@Service
@ConditionalOnProperty(name = "aws.enabled", havingValue = "true")
public class SimpleEmailService implements EmailService {

    private final SesClient sesClient;

    @Autowired
    public SimpleEmailService(AwsCredentialsProvider credentialsProvider, @Value("${aws.region}") String region) {
        this.sesClient = SesClient.builder().region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Override
    public Mono<SESResponse> sendEmail(String to, String subject, String htmlBody) {
        return Mono.fromCallable(() -> {
                    Body body = Body.builder()
                            .html(
                                    Content.builder()
                                            .data(htmlBody)
                                            .charset("UTF-8")
                                            .build()
                            ).build();

                    Content subjectContent = Content.builder()
                            .data(subject)
                            .build();

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
                })
                .onErrorResume(MessageRejectedException.class, rejected -> Mono.just(SESResponse.builder().exception(rejected).build()))
                .onErrorResume(SesException.class, e -> Mono.just(SESResponse.builder().exception(e).build()));
    }

    @Override
    public Flux<SESResponse> sendEmails(List<String> recipients, String subject, String htmlBody) {
        return Flux.fromIterable(recipients)
                .flatMap(recipient -> sendEmail(recipient, subject, htmlBody));
    }
}
