package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "SubmittingTicket Object", description = "")
public class SubmittingTicket {

    private Integer mo_OID; //ticket id

    private Integer mc_OID; //customer id, get from sessioin

    private String orderStatus; // 12 is open

    private Date orderDate; // todo: check if the format is right

    private String customerID;

    private Integer xm_OID;

    private Integer msn_OID;

    private String customerRMA;

    private String customerTerminalID;

    private String customerReportedIssueExt;







}
