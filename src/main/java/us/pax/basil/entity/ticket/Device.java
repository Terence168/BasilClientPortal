package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "Device Object", description = "")
public class Device {

    String serialNumber;

    Date voidDate;

    Date warrantyExpDate;

    String model;

    String version;

    String customerReportedIssue;

    String warrantyStatus;

    Integer cosmeticPrice;

    Integer diagnosticPrice;

    Integer minorPrice;

}
