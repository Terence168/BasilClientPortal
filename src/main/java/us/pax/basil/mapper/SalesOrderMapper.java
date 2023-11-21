package us.pax.basil.mapper;

import us.pax.basil.entity.LabelValuePair;
import us.pax.basil.entity.LabelValuePairStr;
import us.pax.basil.entity.salesorder.SalesOrder;
import us.pax.basil.entity.salesorder.SalesOrderDetail;

import java.util.List;

public interface SalesOrderMapper {
    Integer totalSalesOrders(String salesOrder,
                             String salesStatus,
                             String poNumber,
                             String materialNumber,
                             String description,
                             String salesPerson,
                             String sysproCustomerName,
                             String status,
                             String createFromDate,
                             String createToDate,
                             String shipFromDate,
                             String shipToDate,
                             String customerId);
    
    List<SalesOrder> getSalesOrders(Integer offset,
                                    Integer count,
                                    String sortColumns,
                                    String salesOrder,
                                    String salesStatus,
                                    String poNumber,
                                    String materialNumber,
                                    String description,
                                    String salesPerson,
                                    String sysproCustomerName,
                                    String status,
                                    String createFromDate,
                                    String createToDate,
                                    String shipFromDate,
                                    String shipToDate,
                                    String customerId);
    
    List<SalesOrderDetail> getSalesOrderDetails(Long salesOrder,
                                                String materialNumber,
                                                String description,
                                                String shipFromDate,
                                                String shipToDate);
    
    List<LabelValuePairStr> orderStatusOpt();
}
