package us.pax.basil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.LabelValuePairStr;
import us.pax.basil.mapper.SalesOrderMapper;
import us.pax.basil.service.SalesOrderService;

import java.util.List;

@RestController
@RequestMapping("/sales-order")
public class SalesOrderController {
    
    @Autowired
    private SalesOrderService salesOrderService;
    
    @Autowired
    private SalesOrderMapper salesOrderMapper;
    
    @GetMapping
    public QueryResultDTO findAllSalesOrders(@RequestParam(value = "page", required = false) Integer currentPage,
                                             @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                             @RequestParam(value = "sort", required = false) String sortColumns,
                                             @RequestParam(value = "salesOrder", required = false) String salesOrder,
                                             @RequestParam(value = "salesStatus", required = false) String salesStatus,
                                             @RequestParam(value = "poNumber", required = false) String poNumber,
                                             @RequestParam(value = "materialNumber", required = false) String materialNumber,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "salesPerson", required = false) String salesPerson,
                                             @RequestParam(value = "sysproCustomerName", required = false) String sysproCustomerName,
                                             @RequestParam(value = "status", required = false) String status,
                                             @RequestParam(value = "createDate", required = false) String createDate,
                                             @RequestParam(value = "shipDate", required = false) String shipDate,
                                             @RequestParam(value = "customerId", required = false) String customerId) {
        
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }
        
        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        
        return salesOrderService.getAllSalesOrders(currentPage, sizePerPage, sortColumns, salesOrder, salesStatus, poNumber, materialNumber, description, salesPerson, sysproCustomerName, status, createDate, shipDate, customerId);
    }
    
    @GetMapping("/{salesOrder}")
    public QueryResultDTO findSalesOrderDetails(@PathVariable Long salesOrder,
                                                @RequestParam(value = "materialNumber", required = false) String materialNumber,
                                                @RequestParam(value = "description", required = false) String description,
                                                @RequestParam(value = "shipDate", required = false) String shipDate) {
        
        return salesOrderService.getSalesOrderDetailsDTO(salesOrder, materialNumber, description, shipDate);
    }
    
    @GetMapping("/order-status-options")
    public List<LabelValuePairStr> getOrderStatusOptions() {
        return salesOrderMapper.orderStatusOpt();
    }
}
