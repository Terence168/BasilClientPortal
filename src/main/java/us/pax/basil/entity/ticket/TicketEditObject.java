package us.pax.basil.entity.ticket;

import java.util.List;

public class TicketEditObject {

//    private List<Integer> deleteSerials;//mc_oid
//    private List<SubmittingTicket> updateSerials;
      private List<SubmittingTicket> addSerials;

      private String name;

      private Integer mo_oid;

       //select * from BASIL_SEC_PRD.XREF_INBOUND_TRACKING , MO_OID  check deleteTrackingNums and MO_OID is null, add,
      private List<String> deleteTrackingNums;
      private List<String> addTrackingNums;
      private List<TrackingNumUpdate> updateTrackingNums;



      private List<String> deleteSerials;//mxn_oid
      private List<SNInfo> updateSerials;

      //
}
