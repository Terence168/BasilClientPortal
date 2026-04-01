package us.pax.basil.entity.ticket;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain =  true)
@ApiModel(value = "OrderType Object", description = "")
public class Key{
    private Integer keyIndex;
    private String keyId;
    private String keyCategory;
    private String kcv;
    private String ksi;
    private String keyType;
    private String comment;
}
