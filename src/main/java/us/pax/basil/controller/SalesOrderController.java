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
//    @GetMapping(path = "/excel-export")
//    public void statusExcelExport(@RequestParam(value = "salesOrder", required = false) String salesOrder,
//                                  @RequestParam(value = "salesStatus", required = false) String salesStatus,
//                                  @RequestParam(value = "poNumber", required = false) String poNumber,
//                                  @RequestParam(value = "materialNumber", required = false) String materialNumber,
//                                  @RequestParam(value = "description", required = false) String description,
//                                  @RequestParam(value = "salesPerson", required = false) String salesPerson,
//                                  @RequestParam(value = "sysproCustomerName", required = false) String sysproCustomerName,
//                                  @RequestParam(value = "status", required = false) String status,
//                                  @RequestParam(value = "createDate", required = false) String createDate,
//                                  @RequestParam(value = "shipDate", required = false) String shipDate,
//                                  @RequestParam(value = "customerId", required = false) String customerId,
//                                  HttpServletResponse response) throws IOException {
//
////        ArrayList<StatusExcelExport> data = rmaService.statusExcelExportQuery(partNumber,
////            rmaNumber, serialNumber, customerId);
//
//        try (XSSFWorkbook workBook = new XSSFWorkbook()) {
//            response.setContentType("application/vnd.ms-excel");
//            response.setHeader("Content-Disposition", "attachment;filename=" + "c.xlsx");
//
//            XSSFSheet pivotTableSheet = workBook.createSheet("Summary");
//            XSSFSheet sheet = workBook.createSheet("Status");
//            sheet.setColumnWidth(0, 4000);
//            sheet.setColumnWidth(1, 4000);
//            sheet.setColumnWidth(2, 4000);
//            sheet.setColumnWidth(3, 4000);
//            sheet.setColumnWidth(4, 4000);
//            sheet.setColumnWidth(5, 4000);
//
//            XSSFRow currentRow = sheet.createRow(0);
//            currentRow.createCell(0).setCellValue("Model Number Short");
//            currentRow.createCell(1).setCellValue("RMA Ticket Number");
//            currentRow.createCell(2).setCellValue("Serial Number");
//            currentRow.createCell(3).setCellValue("Customer");
//            currentRow.createCell(4).setCellValue("Status");
//            currentRow.createCell(5).setCellValue("Received Date");
//
//            int i = 1;
//            for (StatusExcelExport statusExcelExport: data) {
//                currentRow = sheet.createRow(i);
//                currentRow.createCell(0).setCellValue(statusExcelExport.getPartNumber());
//                currentRow.createCell(1).setCellValue(statusExcelExport.getRmaNumber());
//                currentRow.createCell(2).setCellValue(statusExcelExport.getSerialNumber());
//                currentRow.createCell(3).setCellValue(statusExcelExport.getCustomerOrganization());
//                currentRow.createCell(4).setCellValue(statusExcelExport.getStatus());
//                currentRow.createCell(5).setCellValue(statusExcelExport.getReceivedDate());
//                i++;
//            }
//
//            CellReference topLeft = new CellReference(sheet.getRow(0).getCell(0));
//            CellReference bottomRight = new CellReference(sheet.getRow(i - 1).getCell(5));
//            AreaReference tableArea = workBook.getCreationHelper().createAreaReference(topLeft, bottomRight);
//            XSSFTable dataTable = sheet.createTable(tableArea);
//            dataTable.setDisplayName("Status");
//
//            //this styles the table as Excel would do per default
//            dataTable.getCTTable().addNewTableStyleInfo();
//            XSSFTableStyleInfo style = (XSSFTableStyleInfo) dataTable.getStyle();
//            style.setName("TableStyleMedium2");
//            style.setShowColumnStripes(false);
//            style.setShowRowStripes(true);
//
//            //this sets auto filters
//            dataTable.getCTTable().addNewAutoFilter().setRef(tableArea.formatAsString());
//
//            // pivot table generation
//            CellReference pos = new CellReference(0, 0);
//            XSSFPivotTable pivotTable = pivotTableSheet.createPivotTable(tableArea, pos);
//
//            pivotTable.addRowLabel(0);
//            pivotTable.addRowLabel(1);
//            pivotTable.addRowLabel(2);
//
//            pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 4, "Count of SNs");
//            pivotTable.addColLabel(4);
//
//            //Method addColLabel removes the dataField setting. So we need set it new.
//            pivotTable.getCTPivotTableDefinition().getPivotFields().getPivotFieldArray(4)
//                .setDataField(true);
//
//            pivotTable.addReportFilter(3);
//
//            try (OutputStream outputStream = response.getOutputStream()) {
//                workBook.write(outputStream);
//            }
//        }
//    }
}
