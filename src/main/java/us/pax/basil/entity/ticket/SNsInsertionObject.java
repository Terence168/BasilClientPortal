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

    private Date orderDate;

    private String orderStatus;

    private String mc_OID;

    private Integer mo_OID; //ticket id

    private String customerID;

    private Integer xm_OID;

    private Integer msn_OID;

    private String customerRMA;

    private String customerTerminalID;

    private String customerReportedIssueExt;
    
}
