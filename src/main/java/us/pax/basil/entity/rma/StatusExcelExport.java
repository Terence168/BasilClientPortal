package us.pax.basil.entity.rma;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RMA Status Excel Export", description="")
public class StatusExcelExport {
    private String partNumber;
    private Long rmaNumber;
    private String serialNumber;
    private String customerOrganization;
    private String status;
    private String receivedDate;
}
