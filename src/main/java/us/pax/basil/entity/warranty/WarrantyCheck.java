package us.pax.basil.entity.warranty;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WarrantyCheck Object", description = "Warranty check information for devices")
public class WarrantyCheck {
    private String serialNumber;
    private String model;
    private String version;
    private String soNum;
    private String poNum;
    private String distName;
    private String distAddress;
    private Date warrantyStartDate;
    private Date warrantyExpDate;
    private String warrantyStatus;
    
    // Additional fields for internal use
    private Integer msnOID;
    private Integer moOID;
    private Date warrantyVoidedDate;
} 