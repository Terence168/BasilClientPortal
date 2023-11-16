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
    private String moDescription;
    private String specialInstructions;
    private String salesPersonInitials;
    private String salesPersonName;
    private String contact;
    private String customerName;
    private String shipAddress3Loc;
    private String shipAddress1;
    private String shipAddress2;
    private String shipAddress3;
    private String shipAddress4;
    private String shipPostalCode;
    private String email;
    private LocalDate reqShipDate;
}
