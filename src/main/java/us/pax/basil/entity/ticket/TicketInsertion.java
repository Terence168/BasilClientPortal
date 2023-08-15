package us.pax.basil.entity.ticket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import us.pax.basil.entity.customer.Address;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
public class TicketInsertion {
    private Integer orderType;
    private List<SNsInsertionObject> serials;
    private List<String>  trackingNumbers;
    private String originalRMA;  //optional, exits if re-repair
    private Address address;
}
