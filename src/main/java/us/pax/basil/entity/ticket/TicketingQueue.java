package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Ticketing Queue", description="")
public class TicketingQueue {
    private String ticketId;
    private String ticketStatus;
    private String department;
    private String customerId;
    private String createdDate;
    private String responder;
}
