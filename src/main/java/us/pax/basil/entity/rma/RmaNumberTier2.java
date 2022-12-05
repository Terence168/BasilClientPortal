package us.pax.basil.entity.rma;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RMA Status RMA Number Tier 2", description="")
public class RmaNumberTier2 {
    private Long rmaNumber;
    private Integer inventory;
    private Integer outForRepair;
    private Integer quarantine;
    private Integer awaitingQaCa;
    private Integer readyToShip;
    private Integer total;
    private String customerOrganization;

}
