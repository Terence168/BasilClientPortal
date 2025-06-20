package us.pax.basil.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.warranty.WarrantyCheck;
import us.pax.basil.service.WarrantyService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Api(tags = "Warranty API Interface")
@RestController
@RequestMapping("/warranty")
public class WarrantyController {

    @Autowired
    private WarrantyService warrantyService;

    @PreAuthorize("hasAuthority('warranty_check')")
    @GetMapping("/check")
    @ApiOperation(value = "Check warranty status by serial number", notes = "Returns warranty information for a single serial number")
    public QueryResultArrayDTO checkWarranty(@RequestParam(value = "serialNumber", required = true) String serialNumber) {
        return warrantyService.checkWarrantyBySerialNumber(serialNumber);
    }

    @PreAuthorize("hasAuthority('warranty_check')")
    @PostMapping("/check/batch")
    @ApiOperation(value = "Check warranty status for multiple serial numbers", notes = "Returns warranty information for multiple serial numbers")
    public QueryResultArrayDTO checkWarrantyBatch(@RequestBody List<String> serialNumbers) {
        return warrantyService.checkWarrantyBatch(serialNumbers);
    }

    @PreAuthorize("hasAuthority('warranty_check')")
    @PostMapping("/check/upload")
    @ApiOperation(value = "Upload file to check warranty status", notes = "Upload Excel file with serial numbers to check warranty status")
    public QueryResultArrayDTO checkWarrantyByFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> serialNumbers = warrantyService.extractSerialNumbersFromFile(file);
        return warrantyService.checkWarrantyBatch(serialNumbers);
    }

    @PreAuthorize("hasAuthority('warranty_check')")
    @GetMapping("/export")
    @ApiOperation(value = "Export warranty check results to Excel", notes = "Export warranty check results to Excel file")
    public void exportWarrantyCheck(@RequestParam(value = "serialNumbers", required = false) List<String> serialNumbers,
                                    HttpServletResponse response) throws IOException {
        
        List<WarrantyCheck> warrantyData = warrantyService.getWarrantyDataForExport(serialNumbers);
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=warranty_check.xlsx");
            
            XSSFSheet sheet = workbook.createSheet("Warranty Check");
            
            // Set column widths
            sheet.setColumnWidth(0, 4000);  // SN
            sheet.setColumnWidth(1, 4000);  // MODEL
            sheet.setColumnWidth(2, 2000);  // VER
            sheet.setColumnWidth(3, 3000);  // SO Num
            sheet.setColumnWidth(4, 3000);  // PO Num
            sheet.setColumnWidth(5, 5000);  // Dist Name
            sheet.setColumnWidth(6, 8000);  // Dist Address
            sheet.setColumnWidth(7, 3500);  // Warranty Start Date
            sheet.setColumnWidth(8, 3500);  // Warranty Exp Date
            sheet.setColumnWidth(9, 4000);  // Warranty Status
            
            // Create header row
            XSSFRow headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("SN");
            headerRow.createCell(1).setCellValue("MODEL");
            headerRow.createCell(2).setCellValue("VER");
            headerRow.createCell(3).setCellValue("SO Num");
            headerRow.createCell(4).setCellValue("PO Num");
            headerRow.createCell(5).setCellValue("Dist Name");
            headerRow.createCell(6).setCellValue("Dist Address");
            headerRow.createCell(7).setCellValue("Warranty Start Date");
            headerRow.createCell(8).setCellValue("Warranty Exp Date");
            headerRow.createCell(9).setCellValue("Warranty Status");
            
            // Add data rows
            int rowNum = 1;
            for (WarrantyCheck warranty : warrantyData) {
                XSSFRow dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(warranty.getSerialNumber());
                dataRow.createCell(1).setCellValue(warranty.getModel());
                dataRow.createCell(2).setCellValue(warranty.getVersion());
                dataRow.createCell(3).setCellValue(warranty.getSoNum() != null ? warranty.getSoNum() : "");
                dataRow.createCell(4).setCellValue(warranty.getPoNum() != null ? warranty.getPoNum() : "");
                dataRow.createCell(5).setCellValue(warranty.getDistName() != null ? warranty.getDistName() : "");
                dataRow.createCell(6).setCellValue(warranty.getDistAddress() != null ? warranty.getDistAddress() : "");
                
                if (warranty.getWarrantyStartDate() != null) {
                    dataRow.createCell(7).setCellValue(warranty.getWarrantyStartDate().toString());
                } else {
                    dataRow.createCell(7).setCellValue("");
                }
                
                if (warranty.getWarrantyExpDate() != null) {
                    dataRow.createCell(8).setCellValue(warranty.getWarrantyExpDate().toString());
                } else {
                    dataRow.createCell(8).setCellValue("");
                }
                
                dataRow.createCell(9).setCellValue(warranty.getWarrantyStatus());
            }
            
            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
        }
    }
} 