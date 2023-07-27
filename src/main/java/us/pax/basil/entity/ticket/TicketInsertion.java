package us.pax.basil.entity.ticket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
public class TicketInsertion {
    private Integer orderType;
    private List<SNsInsertionObject> sNsInsertionObjects;
    private List<String>  trackingNumbers;
    private String originalRMA;  //optional, exits if re-repair
}
