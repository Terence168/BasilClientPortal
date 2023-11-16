package us.pax.basil.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.salesorder.SalesOrder;
import us.pax.basil.mapper.SalesOrderMapper;
import us.pax.basil.service.SalesOrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                             @RequestParam(value = "poNumber", required = false) String poNumber,
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
        
        return salesOrderService.getAllSalesOrders(currentPage, sizePerPage, sortColumns, salesOrder, poNumber, status, createDate, shipDate, customerId);
    }
}
