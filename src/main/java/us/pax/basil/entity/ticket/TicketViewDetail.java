package us.pax.basil.entity.ticket;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Ticketing view detail", description="")
public class TicketViewDetail {
    private String xm_oid;
    private Integer inventory;
    private Integer outForRepair;
    private Integer quarantine;
    private Integer awaitingQaCa;
    private Integer readyToShip;
    private String rmaNumber;
    private String dateReceived;
    private String quarantineDate;
    private String partNumber;
    private String serialNumber;
    private String versionNumber;
    private String partNumber2;
    private String serialNumber2;
    private String versionNumber2;
    private String completedDate;
    private String reportedIssue;
    private String esdKit;
    private String physicalDamagePresent;
    private String customerIssueReproduced;
    private String reportedIssueExt;
    private String tamperLog;
    private String errorMessage;
    private String batteryVoltage;
    private String repairDate;
    private String department;
    private String trackingNumber;
    private String customerName;
    private String assighness;
    private String techNotes;
    private String warrantyEndDate;
    private String warrantyVoidedDate;
    private String order_date;
    private String shipDate;
}
