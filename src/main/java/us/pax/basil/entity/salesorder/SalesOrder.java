package us.pax.basil.entity.salesorder;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesOrder {
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
}
