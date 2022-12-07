package us.pax.basil.entity.rma;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RMA Quarantine Object", description="")
public class Quarantine {
	private String quarantineDate;
	private String partNumber;
	private String serialNumber;
	private Long rmaNumber;
	private String customerContact;
	private String techNotes;
	private String faultCode;
	private String partsNeeded;
	private String customerOrganization;

}
