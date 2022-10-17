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
	private String shipDate;
	private String partNumber;
	private String serialNumber;
	private Long rmaNumber;
	private String trackingNumber;
	private String reportedIssue;
	private String faultCode;

}
