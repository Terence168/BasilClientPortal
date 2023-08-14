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
    private String serialNumber;//MSN_oid

    private Date orderDate;
    private String orderStatus;

//    private String mcOID;
//    private Integer moOID; //ticket id

//    private String customerID;

    private Integer xmOID; //If it's in master_order, fill in it
    private Integer msnOID; //this is the master serial number which used to link to serial number

    private Integer cosmetic;

    private String customerRMA;
    private String customerTerminalID;
    private String customerReportedIssueExt;

    private Date shipDate; //BASIL_ODS_PRD.XREF_SHIP.ship_date
    private String warrantyStatus; //compute by ship_date, warranty_voided_date, warranty_end_date
    private Date warrantyVoidedDate; //msn.WARRANTY_VOIDED_DATE
    private Date warrantyEndDate;//msn.WARRANTY_END_DATE

    private Integer cosmeticPrice;
    private Integer diagnosticPrice;
    private Integer minorPrice;

    private String model;
    private String version;
}
