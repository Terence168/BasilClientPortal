package us.pax.basil.entity.salesorder;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesOrderDetail {
    private Long salesOrderLine;
    private String materialNumber;
    private String productClass;
    private String stockDescription;
    private LocalDate soLineShipDate;
    private String comment;
    private String lineType;
    private String documentType;
    private Integer orderQty;
    private Integer shippedQty;
    private Integer backOrderQty;
    private String lastRefresh;
}
