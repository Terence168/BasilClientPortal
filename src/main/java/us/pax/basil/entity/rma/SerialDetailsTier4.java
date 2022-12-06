package us.pax.basil.entity.rma;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RMA Status Serial Number Details Tier 4", description="")
public class SerialDetailsTier4 {
    private String reportedIssue;
    private String faultCodes;
    private String customerOrganization;
}
