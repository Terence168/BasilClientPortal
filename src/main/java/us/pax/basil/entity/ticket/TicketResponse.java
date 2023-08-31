package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Ticketing response", description="")
public class TicketResponse {
    private Integer rsp_oid;
    private Integer moOID;
    private String responseBy;
    private Date responseDate;
    private String content;
    private String email;
}
