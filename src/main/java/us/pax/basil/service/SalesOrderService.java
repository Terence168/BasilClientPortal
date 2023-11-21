package us.pax.basil.service;

import us.pax.basil.dto.output.QueryResultDTO;

import java.util.List;

public interface SalesOrderService {
    QueryResultDTO getAllSalesOrders(Integer currentPage,
                                     Integer sizePerPage,
                                     String sortColumns,
                                     String salesOrder,
                                     String salesStatus,
                                     String poNumber,
                                     String materialNumber,
                                     String description,
                                     String salesPerson,
                                     String sysproCustomerName,
                                     String status,
                                     String createDate,
                                     String shipDate,
                                     String customerId);
    
    QueryResultDTO getSalesOrderDetailsDTO(Long salesOrder, String materialNumber, String description, String shipDate);
}
