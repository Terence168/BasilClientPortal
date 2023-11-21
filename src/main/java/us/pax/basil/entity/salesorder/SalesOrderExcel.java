package us.pax.basil.entity.salesorder;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesOrderExcel {
    private String salesOrder;
    private String customer;
    private String orderStatus;
    private LocalDate orderDate;
    private String customerPONumber;
    private String description;
    private String specialInstructions;
    private String salesPerson;
    private String contact;
    private String sysproCustomerName;
    private String shipAddress;
    private LocalDate reqShipDate;
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
