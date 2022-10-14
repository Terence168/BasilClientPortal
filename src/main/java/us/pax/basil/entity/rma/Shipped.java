package us.pax.basil.entity.rma;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RMA Shipped Object", description="")
public class Shipped {
	String shipDate;
	String partNumber;
	String serialNumber;
	Long rmaNumber;
	String trackingNumber;
	String reportedIssue;
	String faultCode;

}
