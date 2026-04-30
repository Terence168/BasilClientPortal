package us.pax.basil.entity.rma;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RMA Status Serial Number Tier 3", description="")
public class SerialNumberTier3 {
    private Integer id;
    private String serialNumber;
    private Integer inventory;
    private Integer outForRepair;
    private Integer quarantine;
    private Integer awaitingQaCa;
    private Integer screening;
    private Integer readyToShip;
    private String customerOrganization;
}