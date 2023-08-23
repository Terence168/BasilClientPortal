package us.pax.basil.service.aws.ses;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface EmailService {
    Mono<SESResponse> sendEmail(String to, String subject, String htmlBody);
    Flux<SESResponse> sendEmails(List<String> recipients, String subject, String htmlBody);
}