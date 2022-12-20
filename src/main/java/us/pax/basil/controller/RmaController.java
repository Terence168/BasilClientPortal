package us.pax.basil.controller;
/***
 * ============================================================================
 * = COPYRIGHT Basil
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                     Author                    Action
 * 2020/04/24               yinyy
 * ============================================================================
 */


import io.swagger.annotations.Api;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.rma.Quarantine;
import us.pax.basil.entity.rma.Shipped;
import us.pax.basil.entity.rma.StatusExcelExport;
import us.pax.basil.service.RmaService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = "Basil API Interface")
@RestController
@RequestMapping("/rma")
public class RmaController {

    @Autowired
    private RmaService rmaService;

    // @PreAuthorize("hasAuthority('admin.user.delete')")
    @GetMapping("/status")
    public QueryResultArrayDTO status(@RequestParam(value = "page", required = false) Integer currentPage,
                                       @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                       @RequestParam(value = "sort", required = false) String sortColumns,
                                       @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                       @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                       @RequestParam(value = "partNumber", required = false) String partNumber) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        return rmaService.statusQuery(currentPage, sizePerPage, sortColumns, rmaNumber, serialNumber, partNumber);
    }

    @GetMapping("/status/tier1")
    public QueryResultArrayDTO statusTier1(@RequestParam(value = "partNumber", required = false) String partNumber,
                                           @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                           @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                           @RequestParam(value = "customerId", required = false) String customerId) {
        return rmaService.statusTier1(partNumber, rmaNumber, serialNumber, customerId);
    }

    @GetMapping("/status/tier2")
    public QueryResultArrayDTO statusTier2(@RequestParam(value = "partNumber", required = true) String partNumber,
                                           @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                           @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                           @RequestParam(value = "customerId", required = false) String customerId) {
        return rmaService.statusTier2(partNumber, rmaNumber, serialNumber, customerId);
    }

    @GetMapping("/status/tier3")
    public QueryResultArrayDTO statusTier3(@RequestParam(value = "rmaNumber", required = true) Long rmaNumber,
                                           @RequestParam(value = "partNumber", required = true) String partNumber,
                                           @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                           @RequestParam(value = "customerId", required = false) String customerId) {
        return rmaService.statusTier3(rmaNumber, partNumber, serialNumber, customerId);
    }

    @GetMapping("/status/tier4")
    public QueryResultArrayDTO statusTier4(@RequestParam(value = "id", required = true) Integer id) {
        return rmaService.statusTier4(id);
    }

    // @PreAuthorize("hasAuthority('admin.user.delete')")
    @GetMapping("/shipped")
    public QueryResultArrayDTO shipped (@RequestParam(value = "page", required = false) Integer currentPage,
                                         @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                         @RequestParam(value = "sort", required = false) String sortColumns,
                                         @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                         @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                         @RequestParam(value = "partNumber", required = false) String partNumber,
                                         @RequestParam(value = "shipDate", required = false) String shipDate,
                                         @RequestParam(value = "customerId", required = false) String customerId) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        return rmaService.shippedQuery(currentPage, sizePerPage, sortColumns, rmaNumber, serialNumber, partNumber, shipDate, customerId);
    }

    // @PreAuthorize("hasAuthority('admin.user.delete')")
    @GetMapping("/quarantine")
    public QueryResultArrayDTO quarantine(@RequestParam(value = "page", required = false) Integer currentPage,
                                           @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                           @RequestParam(value = "sort", required = false) String sortColumns,
                                           @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                           @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                           @RequestParam(value = "partNumber", required = false) String partNumber,
                                           @RequestParam(value = "customerId", required = false) String customerId,
                                           @RequestParam(value = "contact", required = false) Integer contact) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        return rmaService.quarantineQuery(currentPage,
                                            sizePerPage,
                                            sortColumns,
                                            rmaNumber,
                                            serialNumber,
                                            partNumber,
                                            customerId,
                                            contact);
    }

    private void writeWorkbook(OutputStream out) throws IOException {
        try (Workbook workBook = new XSSFWorkbook()) {
            Sheet sheet = workBook.createSheet("My Sheet");
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 6000);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("test");
            workBook.write(out);
        }
    }

    @GetMapping(path = "/excel-export/shipping")
    public void shippingExcelExport(@RequestParam(value = "sort", required = false) String sortColumns,
                                     @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                     @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                     @RequestParam(value = "partNumber", required = false) String partNumber,
                                     @RequestParam(value = "shipDate", required = false) String shipDate,
                                     @RequestParam(value = "customerId", required = false) String customerId,
                                     HttpServletResponse response) throws IOException {

        ArrayList<Shipped> data = rmaService.shippedExcelExportQuery(sortColumns,
                rmaNumber, serialNumber, partNumber, shipDate, customerId);

        try (XSSFWorkbook workBook = new XSSFWorkbook()) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + "a.xlsx");

            XSSFSheet sheet = workBook.createSheet("Shipping");
            sheet.setColumnWidth(0, 3000);
            sheet.setColumnWidth(1, 6000);
            sheet.setColumnWidth(2, 6000);
            sheet.setColumnWidth(3, 6000);
            sheet.setColumnWidth(4, 6000);
            sheet.setColumnWidth(5, 12000);
            sheet.setColumnWidth(6, 12000);
            sheet.setColumnWidth(7, 6000);
            sheet.setColumnWidth(8, 6000);

            XSSFRow currentRow = sheet.createRow(0);
            currentRow.createCell(0).setCellValue("Ship Date");
            currentRow.createCell(1).setCellValue("Model Number Short");
            currentRow.createCell(2).setCellValue("Serial Number");
            currentRow.createCell(3).setCellValue("RMA Ticket Number");
            currentRow.createCell(4).setCellValue("Tracking Number");
            currentRow.createCell(5).setCellValue("Customer Reported Issue");
            currentRow.createCell(6).setCellValue("Tech Notes");
            currentRow.createCell(7).setCellValue("Fault Code(s)");
            currentRow.createCell(8).setCellValue("Customer");

            int i = 1;
            XSSFCell reportedIssueCell = null;
            XSSFCell techNotesCell = null;
            CellStyle cellStyle = workBook.createCellStyle(); //Create new style
            cellStyle.setWrapText(true); //Set wordwrap
            for (Shipped shipped: data) {
                currentRow = sheet.createRow(i);
                currentRow.createCell(0).setCellValue(shipped.getShipDate());
                currentRow.createCell(1).setCellValue(shipped.getPartNumber());
                currentRow.createCell(2).setCellValue(shipped.getSerialNumber());
                currentRow.createCell(3).setCellValue(shipped.getRmaNumber());
                currentRow.createCell(4).setCellValue(shipped.getTrackingNumber());

                reportedIssueCell = currentRow.createCell(5);
                reportedIssueCell.setCellStyle(cellStyle);
                reportedIssueCell.setCellValue(shipped.getReportedIssue());

                techNotesCell = currentRow.createCell(6);
                techNotesCell.setCellStyle(cellStyle);
                techNotesCell.setCellValue(shipped.getTechNotes());

                currentRow.createCell(7).setCellValue(shipped.getFaultCode());
                currentRow.createCell(8).setCellValue(shipped.getCustomerOrganization());
                i++;
            }

            CellReference topLeft = new CellReference(sheet.getRow(0).getCell(0));
            CellReference bottomRight = new CellReference(sheet.getRow(i - 1).getCell(8));
            AreaReference tableArea = workBook.getCreationHelper().createAreaReference(topLeft, bottomRight);
            XSSFTable dataTable = sheet.createTable(tableArea);
            dataTable.setDisplayName("Shipped");

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

    @GetMapping(path = "/excel-export/quarantine")
    public void quarantineExcelExport(@RequestParam(value = "sort", required = false) String sortColumns,
                                      @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                      @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                      @RequestParam(value = "partNumber", required = false) String partNumber,
                                      @RequestParam(value = "customerId", required = false) String customerId,
                                      @RequestParam(value = "contact", required = false) Integer contact,
                                      HttpServletResponse response) throws IOException {

        ArrayList<Quarantine> data = rmaService.quarantineExcelExportQuery(sortColumns,
                rmaNumber, serialNumber, partNumber, customerId, contact);

        try (XSSFWorkbook workBook = new XSSFWorkbook()) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + "b.xlsx");

            XSSFSheet sheet = workBook.createSheet("Quarantine");
            sheet.setColumnWidth(0, 3000);
            sheet.setColumnWidth(1, 6000);
            sheet.setColumnWidth(2, 6000);
            sheet.setColumnWidth(3, 6000);
            sheet.setColumnWidth(4, 6000);
            sheet.setColumnWidth(5, 12000);
            sheet.setColumnWidth(6, 12000);
            sheet.setColumnWidth(7, 12000);
            sheet.setColumnWidth(8, 6000);

            XSSFRow currentRow = sheet.createRow(0);
            currentRow.createCell(0).setCellValue("Quarantine Date");
            currentRow.createCell(1).setCellValue("Model Number Short");
            currentRow.createCell(2).setCellValue("Serial Number");
            currentRow.createCell(3).setCellValue("RMA Ticket Number");
            currentRow.createCell(4).setCellValue("Customer Contact Needed");
            currentRow.createCell(5).setCellValue("Tech Notes");
            currentRow.createCell(6).setCellValue("Fault Code(s)");
            currentRow.createCell(7).setCellValue("Part(s) Needed");
            currentRow.createCell(8).setCellValue("Customer");

            int i = 1;
            XSSFCell techNotesCell = null;
            CellStyle cellStyle = workBook.createCellStyle(); //Create new style
            cellStyle.setWrapText(true); //Set wordwrap
            for (Quarantine quarantine: data) {
                currentRow = sheet.createRow(i);
                currentRow.createCell(0).setCellValue(quarantine.getQuarantineDate());
                currentRow.createCell(1).setCellValue(quarantine.getPartNumber());
                currentRow.createCell(2).setCellValue(quarantine.getSerialNumber());
                currentRow.createCell(3).setCellValue(quarantine.getRmaNumber());
                currentRow.createCell(4).setCellValue(quarantine.getCustomerContact());

                techNotesCell = currentRow.createCell(5);
                techNotesCell.setCellStyle(cellStyle);
                techNotesCell.setCellValue(quarantine.getTechNotes());

                currentRow.createCell(6).setCellValue(quarantine.getFaultCode());
                currentRow.createCell(7).setCellValue(quarantine.getPartsNeeded());
                currentRow.createCell(8).setCellValue(quarantine.getCustomerOrganization());
                i++;
            }

            CellReference topLeft = new CellReference(sheet.getRow(0).getCell(0));
            CellReference bottomRight = new CellReference(sheet.getRow(i - 1).getCell(8));
            AreaReference tableArea = workBook.getCreationHelper().createAreaReference(topLeft, bottomRight);
            XSSFTable dataTable = sheet.createTable(tableArea);
            dataTable.setDisplayName("Quarantine");

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

    @GetMapping(path = "/excel-export/status")
    public void statusExcelExport(@RequestParam(value = "partNumber", required = false) String partNumber,
                                  @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                  @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                  @RequestParam(value = "customerId", required = false) String customerId,
                                  HttpServletResponse response) throws IOException {

        ArrayList<StatusExcelExport> data = rmaService.statusExcelExportQuery(partNumber,
                rmaNumber, serialNumber, customerId);

        try (XSSFWorkbook workBook = new XSSFWorkbook()) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + "c.xlsx");

            XSSFSheet pivotTableSheet = workBook.createSheet("Summary");
            XSSFSheet sheet = workBook.createSheet("Status");
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);

            XSSFRow currentRow = sheet.createRow(0);
            currentRow.createCell(0).setCellValue("Model Number Short");
            currentRow.createCell(1).setCellValue("RMA Ticket Number");
            currentRow.createCell(2).setCellValue("Serial Number");
            currentRow.createCell(3).setCellValue("Customer");
            currentRow.createCell(4).setCellValue("Status");

            int i = 1;
            for (StatusExcelExport statusExcelExport: data) {
                currentRow = sheet.createRow(i);
                currentRow.createCell(0).setCellValue(statusExcelExport.getPartNumber());
                currentRow.createCell(1).setCellValue(statusExcelExport.getRmaNumber());
                currentRow.createCell(2).setCellValue(statusExcelExport.getSerialNumber());
                currentRow.createCell(3).setCellValue(statusExcelExport.getCustomerOrganization());
                currentRow.createCell(4).setCellValue(statusExcelExport.getStatus());
                i++;
            }

            CellReference topLeft = new CellReference(sheet.getRow(0).getCell(0));
            CellReference bottomRight = new CellReference(sheet.getRow(i - 1).getCell(4));
            AreaReference tableArea = workBook.getCreationHelper().createAreaReference(topLeft, bottomRight);
            XSSFTable dataTable = sheet.createTable(tableArea);
            dataTable.setDisplayName("Status");

            //this styles the table as Excel would do per default
            dataTable.getCTTable().addNewTableStyleInfo();
            XSSFTableStyleInfo style = (XSSFTableStyleInfo) dataTable.getStyle();
            style.setName("TableStyleMedium2");
            style.setShowColumnStripes(false);
            style.setShowRowStripes(true);

            //this sets auto filters
            dataTable.getCTTable().addNewAutoFilter().setRef(tableArea.formatAsString());

            // pivot table generation
            CellReference pos = new CellReference(0, 0);
            XSSFPivotTable pivotTable = pivotTableSheet.createPivotTable(tableArea, pos);

            pivotTable.addRowLabel(0);
            pivotTable.addRowLabel(1);
            pivotTable.addRowLabel(2);

            pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 4, "Count of SNs");
            pivotTable.addColLabel(4);

            //Method addColLabel removes the dataField setting. So we need set it new.
            pivotTable.getCTPivotTableDefinition().getPivotFields().getPivotFieldArray(4)
                    .setDataField(true);

            pivotTable.addReportFilter(3);

            try (OutputStream outputStream = response.getOutputStream()) {
                workBook.write(outputStream);
            }
        }
    }
}
