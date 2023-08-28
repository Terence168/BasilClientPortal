package us.pax.basil.service.aws.ses;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@ConditionalOnProperty(name = "aws.enabled", havingValue = "false", matchIfMissing = true)
public class DummyEmailService implements EmailService {

    @Override
    public Mono<SESResponse> sendEmail(String to, String subject, String htmlBody) {
        return Mono.just(SESResponse.builder().build());
    }

    @Override
    public Flux<SESResponse> sendEmails(List<String> recipients, String subject, String htmlBody) {
        return Flux.just(SESResponse.builder().build());
    }
}
