package us.pax.basil.service.aws.ses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DefaultEmailService implements EmailService {

    @Override
    public CompletableFuture<SESResponse> sendEmail(String to, String subject, String htmlBody) {
        return CompletableFuture.completedFuture(SESResponse.builder().build());
    }

    @Override
    public CompletableFuture<SESResponse> sendEmailWithAttachment(String to,
                                                                  String subject,
                                                                  String htmlBody,
                                                                  String attachmentFileName,
                                                                  String attachmentContentType,
                                                                  byte[] attachmentBytes) {
        return CompletableFuture.completedFuture(SESResponse.builder().build());
    }

    @Override
    public CompletableFuture<List<SESResponse>> sendEmails(List<String> recipients, String subject, String htmlBody) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<List<SESResponse>> sendTemplatedEmail(String subject, String templateName, Map<String, Object> model, String... recipients) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }
}
