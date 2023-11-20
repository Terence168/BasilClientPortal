package us.pax.basil.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.salesorder.SalesOrder;
import us.pax.basil.mapper.SalesOrderMapper;
import us.pax.basil.service.SalesOrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {
    
    private final SalesOrderMapper salesOrderMapper;
    
    @Transactional(readOnly = true)
    public QueryResultDTO getAllSalesOrders(Integer currentPage,
                                            Integer sizePerPage,
                                            String sortColumns,
                                            String salesOrder,
                                            String salesStatus,
                                            String poNumber,
                                            String materialNumber,
                                            String description,
                                            String salesPerson,
                                            String status,
                                            String createDate,
                                            String shipDate,
                                            String customerId) {
        String[] shipDates;
        String shipFromDate = null;
        String shipToDate = null;
        
        if (shipDate != null) {
            shipDates = shipDate.split(" ~ ");
            shipFromDate = shipDates[0];
            shipToDate = shipDates[1];
        }
        
        String[] createDates;
        String createFromDate = null;
        String createToDate = null;
        
        if (createDate != null) {
            createDates = createDate.split(" ~ ");
            createFromDate = createDates[0];
            createToDate = createDates[1];
        }
        
        
        Map<String, Object> data = new HashMap<>();
        data.put("total", salesOrderMapper.totalSalesOrders(salesOrder, salesStatus, poNumber, materialNumber, description, salesPerson, status, createFromDate, createToDate, shipFromDate, shipToDate, customerId));
        data.put("salesOrders", salesOrderMapper.getSalesOrders((currentPage - 1) * sizePerPage, sizePerPage, sortColumns, salesOrder, salesStatus, poNumber, materialNumber, description, salesPerson, status, createFromDate, createToDate, shipFromDate, shipToDate, customerId));
        return new QueryResultDTO(data, 0, "");
    }
    
    @Transactional(readOnly = true)
    public QueryResultDTO getSalesOrderDetailsDTO(Long salesOrder, String materialNumber, String description, String shipDate) {
        String[] shipDates;
        String shipFromDate = null;
        String shipToDate = null;
        
        if (shipDate != null) {
            shipDates = shipDate.split(" ~ ");
            shipFromDate = shipDates[0];
            shipToDate = shipDates[1];
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("salesOrderDetails", salesOrderMapper.getSalesOrderDetails(salesOrder, materialNumber, description, shipFromDate, shipToDate));
        return new QueryResultDTO(data, 0, "");
    }
}
