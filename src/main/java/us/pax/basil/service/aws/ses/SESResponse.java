package us.pax.basil.service.aws.ses;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

/**
 * Simple Email Service Response DTO
 * <br><br>
 * boolean success                      - if the response was successful or not<br>
 * {@link SdkException} exception       - if null and unsuccessful service is disabled<br>
 * {@link SendEmailResponse} response   - the response from AWS (AWS SDKv2 documentation)
 */
@Builder
@Data
public class SESResponse {

    @Builder.Default
    private boolean success = false;

    @Builder.Default
    private SdkException exception = null;

    @Builder.Default
    private SendEmailResponse response = null;
}