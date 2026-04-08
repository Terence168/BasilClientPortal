package us.pax.basil.entity.ticket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import us.pax.basil.entity.customer.Address;

import javax.swing.text.StyledEditorKit;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
public class TicketInsertion {
    private Integer mcOID;
    private Integer orderDept;
    // Backward compatibility for old clients that still submit "orderType".
    private Integer orderType;
    private List<SNsInsertionObject> serials;
    private List<String>  trackingNumbers;
    private String originalRMA;  //optional, exits if re-repair
    private Integer xaOID; //address id
    private Boolean cosmetic;//if user choose cosmetic, true
    private String encrypt;
    private String testKeyType;
    private List<Integer> keyIndexes;
    private String remark;
}
