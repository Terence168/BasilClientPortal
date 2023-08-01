package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="View Ticketing", description="")
public class TicketInfo {
    private Integer moOID; //ticket id
    private Boolean isFromMaster; //if it's from master_order, mark it as true, else false
    private String orderStatus;//from (prep_)master_order ORDER_STATUS
    private Date createDate;//from (prep_)master_order Order_Date
    private String organization;//submitters' org, not sure where to get it
    private String submitterEmail;//submitters' email, not sure
    private String submitterName;//submitters' name, not sure
    private Integer typeOfRepair; //use mo_oid to query from BASIL_ODS_PRD.MASTER_ORDER and BASIL_SEC_PRD.PREP_MASTER_ORDER
    private String address; //use mo_oid to query from BASIL_ODS_PRD.MASTER_ORDER and BASIL_SEC_PRD.PREP_MASTER_ORDER
    private List<String> trackingNumbers; //use mo_oid to query from BASIL_SEC_PRD.XREF_INBOUND_TRACKING
    private List<SNInfo> serials; //use mo_oid to query from BASIL_SEC_PRD.PREP_XREF_MATERIALS and BASIL_ODS_PRD.XREF_MATERIALS
}
