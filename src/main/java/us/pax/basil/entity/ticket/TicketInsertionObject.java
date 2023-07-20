package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "TicketInsertion Object", description = "")
public class TicketInsertionObject {


    // this object is used to insert ticket into prep_master_order database
    private Integer mo_OID; //ticket id

    private Integer mc_OID; //customer id, get from sessioin

    private String orderStatus; // 12 is open

    private Date orderDate; // todo: check if the format is right

    public void setOrderDateToCurrentDate(){
        this.orderDate = new Date();
    }

}
