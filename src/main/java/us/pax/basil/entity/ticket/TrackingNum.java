package us.pax.basil.entity.ticket;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Tracking Number", description="")
public class TrackingNum {
    private String num;
    private Integer xitOID;
}
