package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import us.pax.basil.entity.customer.Address;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Ticketing edit", description="")
public class TicketEditDTO {
    private Address address;
    private Integer mcOID;
    private boolean isFromMaster;
    private Integer typeOfRepair;
    private String originalRMA;
    private String encrypt;
    private String testKeyType;
    private List<Integer> keyIndexes;
    private List<String> deleteSerial; //xm_oid
    private List<SNsInsertionObject> addSerial;
    private List<SNsInsertionObject> updateSerial;

    private List<TrackingNum> addTracking; //<mo_oid, new_number>
    private List<String> deleteTracking; //xit_oid
    private List<TrackingNum> updateTracking;//<xit_oid, new_number>
}
