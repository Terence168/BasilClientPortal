package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "SerialNumbers Information Object", description = "")
public class SNInfo {
    //insert rows into PREP_XREF_MATERIALS
    private String serialNumber;

    private Date orderDate;

    private String orderStatus;

    private String mcOID;

    private Integer moOID; //ticket id

    private String customerID;

    private Integer xmOID; //If it's in master_order, fill in it

    private Integer pxmOID; //if it's in prep_master order, fill in it

    private Integer msnOID; //this is the master serial number which used to link to serial number

    private String customerRMA;

    private String customerTerminalID;

    private String customerReportedIssueExt;

}
