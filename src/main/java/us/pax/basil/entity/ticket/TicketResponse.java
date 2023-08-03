package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Ticketing response", description="")
public class TicketResponse {
    private Integer rsp_oid;
    private String moId;
    private String response;
    private String responseDate;
    private String content;
}
