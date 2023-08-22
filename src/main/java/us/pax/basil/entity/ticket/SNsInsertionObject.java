package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "SerialNumbers Insertion Object", description = "")
public class SNsInsertionObject {
    //insert rows into PREP_XREF_MATERIALS
    private String serialNumber;

    private Date orderDate;

    private String orderStatus;

    private String mcOID;

    private Integer moOID; //ticket id

    private Integer xmOID;

    private Integer msnOID; //this is the master serial number which used to link to serial number

    private String customerRMA;

    private String customerTerminalID;

    private String customerReportedIssueExt;
    private Boolean cosmetic;

}
