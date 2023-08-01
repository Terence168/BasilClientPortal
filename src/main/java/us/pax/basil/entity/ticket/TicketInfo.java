package us.pax.basil.entity.ticket;

import java.util.List;

public class TicketInfo {
    private Integer moOID; //ticket id
    private String orderStatus;//from (prep_)master_order ORDER_STATUS
    private Boolean isFromMaster; //if it's from master_order, mark it as true, else false
    private Integer typeOfRepair; //use mo_oid to query from BASIL_ODS_PRD.MASTER_ORDER and BASIL_SEC_PRD.PREP_MASTER_ORDER
    private String address; //use mo_oid to query from BASIL_ODS_PRD.MASTER_ORDER and BASIL_SEC_PRD.PREP_MASTER_ORDER
    private List<String> trackingNumbers; //use mo_oid to query from BASIL_SEC_PRD.XREF_INBOUND_TRACKING
    private List<SNInfo> serials; //use mo_oid to query from BASIL_SEC_PRD.PREP_XREF_MATERIALS and BASIL_ODS_PRD.XREF_MATERIALS
}
