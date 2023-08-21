package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import us.pax.basil.entity.customer.Address;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "TicketInsertion Object", description = "")
public class TicketInsertionObject {

    // this object is used to insert tickets into prep_master_order database
    private Integer moOID; //ticket id

    private Integer mcOID; //customer id, get from session

    private String orderStatus; // 12 is open

    private Date orderDate;

    private Integer orderType;

    private String rmaNumber;
    private Integer submitterID;
    private Integer xaOID;
    public void setOrderDateToCurrentDate() {
        this.orderDate = new Date();
    }


}
