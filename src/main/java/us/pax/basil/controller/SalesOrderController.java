package us.pax.basil.controller;

import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.LabelValuePairStr;
import us.pax.basil.entity.rma.StatusExcelExport;
import us.pax.basil.entity.salesorder.SalesOrderExcel;
import us.pax.basil.mapper.SalesOrderMapper;
import us.pax.basil.service.SalesOrderService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
    
    //    @PreAuthorize("hasAuthority('status')")
    @GetMapping(path = "/excel-export")
    public void statusExcelExport(@RequestParam(value = "salesOrder", required = false) String salesOrder,
                                  @RequestParam(value = "salesStatus", required = false) String salesStatus,
                                  @RequestParam(value = "poNumber", required = false) String poNumber,
                                  @RequestParam(value = "materialNumber", required = false) String materialNumber,
                                  @RequestParam(value = "description", required = false) String description,
                                  @RequestParam(value = "salesPerson", required = false) String salesPerson,
                                  @RequestParam(value = "sysproCustomerName", required = false) String sysproCustomerName,
                                  @RequestParam(value = "status", required = false) String status,
                                  @RequestParam(value = "createDate", required = false) String createDate,
                                  @RequestParam(value = "shipDate", required = false) String shipDate,
                                  @RequestParam(value = "customerId", required = false) String customerId,
                                  HttpServletResponse response) throws IOException {
        
        List<SalesOrderExcel> data = salesOrderService.getSalesOrdersExcelExport(salesOrder, salesStatus, poNumber, materialNumber, description, salesPerson, sysproCustomerName, status, createDate, shipDate, customerId);
        
        try (XSSFWorkbook workBook = new XSSFWorkbook()) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + "sales-order.xlsx");
            
            XSSFSheet sheet = workBook.createSheet("Sales Orders");
            
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 6000);
            sheet.setColumnWidth(5, 4000);
            
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 5000);
            sheet.setColumnWidth(8, 10000);
            sheet.setColumnWidth(9, 10000);
            sheet.setColumnWidth(10, 12000);
            sheet.setColumnWidth(11, 4000);
            
            sheet.setColumnWidth(12, 2000);
            sheet.setColumnWidth(13, 4000);
            sheet.setColumnWidth(14, 4000);
            sheet.setColumnWidth(15, 10000);
            sheet.setColumnWidth(16, 4000);
            sheet.setColumnWidth(17, 8000);
            
            sheet.setColumnWidth(18, 6000);
            sheet.setColumnWidth(19, 4000);
            sheet.setColumnWidth(20, 2000);
            sheet.setColumnWidth(21, 2000);
            sheet.setColumnWidth(22, 2000);
            sheet.setColumnWidth(23, 5000);
            
            XSSFRow currentRow = sheet.createRow(0);
            
            currentRow.createCell(0).setCellValue("Sales Order");
            currentRow.createCell(1).setCellValue("Customer");
            currentRow.createCell(2).setCellValue("Order Status");
            currentRow.createCell(3).setCellValue("Order Date");
            currentRow.createCell(4).setCellValue("PO Number");
            currentRow.createCell(5).setCellValue("Description");
            
            currentRow.createCell(6).setCellValue("Special Instructions");
            currentRow.createCell(7).setCellValue("Sales Person");
            currentRow.createCell(8).setCellValue("Contact");
            currentRow.createCell(9).setCellValue("Syspro Customer Name");
            currentRow.createCell(10).setCellValue("Ship Address");
            currentRow.createCell(11).setCellValue("Requested Ship Date");
            
            currentRow.createCell(12).setCellValue("Line");
            currentRow.createCell(13).setCellValue("Material Number");
            currentRow.createCell(14).setCellValue("Product Class");
            currentRow.createCell(15).setCellValue("Stock Description");
            currentRow.createCell(16).setCellValue("Ship Date");
            currentRow.createCell(17).setCellValue("Comment");
            
            currentRow.createCell(18).setCellValue("Line Type");
            currentRow.createCell(19).setCellValue("Document Type");
            currentRow.createCell(20).setCellValue("Order Qty");
            currentRow.createCell(21).setCellValue("Shipped Qty");
            currentRow.createCell(22).setCellValue("Back Order Qty");
            currentRow.createCell(23).setCellValue("Last Refresh");
            
            int i = 1;
            for (SalesOrderExcel salesOrderExcel : data) {
                currentRow = sheet.createRow(i);
                currentRow.createCell(0).setCellValue(salesOrderExcel.getSalesOrder());
                currentRow.createCell(1).setCellValue(salesOrderExcel.getCustomer());
                currentRow.createCell(2).setCellValue(salesOrderExcel.getOrderStatus());
                currentRow.createCell(3).setCellValue(salesOrderExcel.getOrderDate() != null ? salesOrderExcel.getOrderDate().toString() : "");
                currentRow.createCell(4).setCellValue(salesOrderExcel.getCustomerPONumber());
                currentRow.createCell(5).setCellValue(salesOrderExcel.getDescription());
                
                currentRow.createCell(6).setCellValue(salesOrderExcel.getSpecialInstructions());
                currentRow.createCell(7).setCellValue(salesOrderExcel.getSalesPerson());
                currentRow.createCell(8).setCellValue(salesOrderExcel.getContact());
                currentRow.createCell(9).setCellValue(salesOrderExcel.getSysproCustomerName());
                currentRow.createCell(10).setCellValue(salesOrderExcel.getShipAddress());
                currentRow.createCell(11).setCellValue(salesOrderExcel.getReqShipDate() != null ? salesOrderExcel.getReqShipDate().toString() : "");
                
                currentRow.createCell(12).setCellValue(salesOrderExcel.getSalesOrderLine() != null ? salesOrderExcel.getSalesOrderLine().toString() : "");
                currentRow.createCell(13).setCellValue(salesOrderExcel.getMaterialNumber());
                currentRow.createCell(14).setCellValue(salesOrderExcel.getProductClass());
                currentRow.createCell(15).setCellValue(salesOrderExcel.getStockDescription());
                currentRow.createCell(16).setCellValue(salesOrderExcel.getSoLineShipDate() != null ? salesOrderExcel.getSoLineShipDate().toString() : "");
                currentRow.createCell(17).setCellValue(salesOrderExcel.getComment());
                
                currentRow.createCell(18).setCellValue(salesOrderExcel.getLineType());
                currentRow.createCell(19).setCellValue(salesOrderExcel.getDocumentType());
                currentRow.createCell(20).setCellValue(salesOrderExcel.getOrderQty() != null ? salesOrderExcel.getOrderQty().toString() : "");
                currentRow.createCell(21).setCellValue(salesOrderExcel.getShippedQty() != null ? salesOrderExcel.getShippedQty().toString() : "");
                currentRow.createCell(22).setCellValue(salesOrderExcel.getBackOrderQty() != null ? salesOrderExcel.getBackOrderQty().toString() : "");
                currentRow.createCell(23).setCellValue(salesOrderExcel.getLastRefresh());
                i++;
            }
            
            CellReference topLeft = new CellReference(sheet.getRow(0).getCell(0));
            CellReference bottomRight = new CellReference(sheet.getRow(i - 1).getCell(23));
            AreaReference tableArea = workBook.getCreationHelper().createAreaReference(topLeft, bottomRight);
            XSSFTable dataTable = sheet.createTable(tableArea);
            dataTable.setDisplayName("SalesOrderTable");
            
            //this styles the table as Excel would do per default
            dataTable.getCTTable().addNewTableStyleInfo();
            XSSFTableStyleInfo style = (XSSFTableStyleInfo) dataTable.getStyle();
            style.setName("TableStyleMedium2");
            style.setShowColumnStripes(false);
            style.setShowRowStripes(true);
            
            //this sets auto filters
            dataTable.getCTTable().addNewAutoFilter().setRef(tableArea.formatAsString());
            
            try (OutputStream outputStream = response.getOutputStream()) {
                workBook.write(outputStream);
            }
        }
    }
}
