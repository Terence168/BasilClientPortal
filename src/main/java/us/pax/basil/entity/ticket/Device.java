package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "Device Object", description = "")
public class Device {

    private String serialNumber;

    private Date voidDate;

    private Date warrantyExpDate;

    private String model;

    private String version;

    private String customerReportedIssue;

    private String warrantyStatus;

    private Integer cosmeticPrice;

    private Integer diagnosticPrice;

    private Boolean existInAnotherTicket;

    private Integer minorPrice;

    private String xm_OID;

}
