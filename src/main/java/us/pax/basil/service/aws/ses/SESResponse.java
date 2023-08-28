package us.pax.basil.service.aws.ses;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

@Builder
@Data
public class SESResponse {

    @Builder.Default
    private boolean success = false;

    @Builder.Default
    private SesException exception = null;

    @Builder.Default
    private SendEmailResponse response = null;
}
