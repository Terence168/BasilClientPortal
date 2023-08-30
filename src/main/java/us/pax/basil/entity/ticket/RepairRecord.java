package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "Repair Record Object", description = "")
public class RepairRecord {
    private String id; //xref_material.xm_oid, int pk
    private Integer rmaNumber; //master_order.mo_oid, int pk

    private String status; //xref_material.status left join attribute
    private Date orderDate;//mo.orderDate
    private Date receivedDate;
    private Date repairDate; //xm.REPAIR_DATE, date
    private Date completedDate; // xref_material.repair_date, date
    private Date quarantineDate;//xref_material.quarantine_date, date
    private Date scheduledDate; //BASIL_ODS_PRD.XREF_SHIP.SCHEDULED_DATE

    private String partNumber; //master_part.part_number_long, varchar(23)
    private String serialNumber;//master_serial_number.serial_number, varchar(17)
    private String versionNumber;//master_part.version_number, varchar(3)

    private String partNumber2; //master_part.part_number_long, varchar(23)
    private String serialNumber2;//master_serial_number.serial_number, varchar(17)
    private String versionNumber2;//master_part.version_number, varchar(3)

    private String customerReportedIssue; // support_attribute_values, varchar(100)
    private String esdKit; // BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES,
    private String physicalDamagePresent ;// BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES
    private String customerIssueReproduced;// BASIL_ODS_PRD.SUPPORT_ATTRIBUTE_VALUES xm.CUSTOMER_ISSUE_REPRODUCED on sav_oid

    private String reportedIssueExt;//xref_material.CUSTOMER_REPORTED_ISSUE_EXT
    private String tamperLog ; //xm.TAMPER_LOG_INTERPRETATION
    private String errorMessage ;
    private String batteryVoltage;
    private String department ;
    private String trackingNumber; //BASIL_ODS_PRD.XREF_SHIP.TRACKING_NUMBER
    private String customerName ; //BASIL_ODS_PRD.MASTER_CUSTOMER.CUSTOMER_ORGANIZATION

    private String assignee; //EMPLOYEE_MASTER.name
    private String techNotes;//xm.TECH_NOTES

    private Date shipDate; //BASIL_ODS_PRD.XREF_SHIP.ship_date
    private String warrantyStatus; //compute by ship_date, warranty_voided_date, warranty_end_date
    private Date warrantyVoidedDate; //msn.WARRANTY_VOIDED_DATE
    private Date warrantyEndDate;//msn.WARRANTY_END_DATE

}
