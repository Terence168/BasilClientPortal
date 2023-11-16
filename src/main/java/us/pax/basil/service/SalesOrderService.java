package us.pax.basil.service;

import us.pax.basil.dto.output.QueryResultDTO;

import java.util.List;

public interface SalesOrderService {
    QueryResultDTO getAllSalesOrders(Integer currentPage,
                                     Integer sizePerPage,
                                     String sortColumns,
                                     String salesOrder,
                                     String poNumber,
                                     String status,
                                     String createDate,
                                     String shipDate,
                                     String customerId);
}
