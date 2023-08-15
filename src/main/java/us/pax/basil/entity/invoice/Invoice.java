package us.pax.basil.entity.invoice;

import lombok.Data;

import java.util.Date;


@Data
public class Invoice {
    private Integer fiOID;
    private Integer xmOID;
    private Integer moOID;
    private Float diagnostic;
    private Float cosmetic;
    private Float minor;
    private Float major;
    private Date create_date;
    private Date update_date;
    private Float inv1ItemTotal;

}
