package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Handle Ticketing", description="")
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
