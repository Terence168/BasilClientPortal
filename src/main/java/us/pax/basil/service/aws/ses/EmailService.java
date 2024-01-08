package us.pax.basil.service.aws.ses;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface EmailService {

    CompletableFuture<SESResponse> sendEmail(String to, String subject, String htmlBody);

    CompletableFuture<List<SESResponse>> sendEmails(List<String> recipients, String subject, String htmlBody);

    CompletableFuture<List<SESResponse>> sendTemplatedEmail(String subject, String templateName, Map<String, Object> model, String... recipients);
}