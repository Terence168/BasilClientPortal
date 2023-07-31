package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Ticketing view", description="")
public class TicketView {

    private String ticketId;
    private String status;
    private String department;
    private String type;
    private String createdDate;

    private String customerOrganization;

    private String lastResponse;
    private String responder;
}
