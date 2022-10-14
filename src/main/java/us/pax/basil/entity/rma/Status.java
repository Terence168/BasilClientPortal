package us.pax.basil.entity.rma;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RMA Status Object", description="")
public class Status {
    String shipDate;
    String partNumber;
    String serialNumber;
    Integer moOid;
    String trackingNumber;
    String reportedIssue;
    String faultCode;

}
