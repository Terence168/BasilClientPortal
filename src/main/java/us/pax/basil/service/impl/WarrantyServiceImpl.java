package us.pax.basil.service.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.warranty.WarrantyCheck;
import us.pax.basil.mapper.WarrantyMapper;
import us.pax.basil.service.WarrantyService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service("warrantyService")
public class WarrantyServiceImpl implements WarrantyService {

    @Autowired
    private WarrantyMapper warrantyMapper;

    @Override
    public QueryResultArrayDTO checkWarrantyBySerialNumber(String serialNumber) {
        List<String> serialNumbers = Collections.singletonList(serialNumber);
        return checkWarrantyBatch(serialNumbers);
    }

    @Override
    public QueryResultArrayDTO checkWarrantyBatch(List<String> serialNumbers) {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        int resultCode = 0;
        String errorMessage = "";
        
        try {
            // Query warranty information for all serial numbers
            List<WarrantyCheck> warrantyChecks = warrantyMapper.getWarrantyCheckBySerialNumbers(serialNumbers);
            
            // Convert to Map for QueryResultArrayDTO
            for (WarrantyCheck check : warrantyChecks) {
                Map<String, Object> warrantyMap = new HashMap<>();
                warrantyMap.put("serialNumber", check.getSerialNumber());
                warrantyMap.put("model", check.getModel());
                warrantyMap.put("version", check.getVersion());
                warrantyMap.put("soNum", check.getSoNum());
                warrantyMap.put("poNum", check.getPoNum());
                warrantyMap.put("distName", check.getDistName());
                warrantyMap.put("distAddress", check.getDistAddress());
                warrantyMap.put("warrantyStartDate", check.getWarrantyStartDate());
                warrantyMap.put("warrantyExpDate", check.getWarrantyExpDate());
                warrantyMap.put("warrantyStatus", check.getWarrantyStatus());
                data.add(warrantyMap);
            }
            
        } catch (Exception e) {
            log.error("Error checking warranty: ", e);
            resultCode = -1;
            errorMessage = "Error checking warranty: " + e.getMessage();
        }
        
        return new QueryResultArrayDTO(data, data.size(), resultCode, errorMessage);
    }

    @Override
    public List<String> extractSerialNumbersFromFile(MultipartFile file) throws IOException {
        List<String> serialNumbers = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                // Skip header row
                if (row.getRowNum() == 0) {
                    continue;
                }
                
                Cell cell = row.getCell(0); // Assuming serial numbers are in the first column
                if (cell != null) {
                    String serialNumber = getCellValueAsString(cell);
                    if (serialNumber != null && !serialNumber.trim().isEmpty()) {
                        serialNumbers.add(serialNumber.trim());
                    }
                }
            }
        }
        
        return serialNumbers;
    }

    @Override
    public List<WarrantyCheck> getWarrantyDataForExport(List<String> serialNumbers) {
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Query already includes calculated warranty status
        return warrantyMapper.getWarrantyCheckBySerialNumbers(serialNumbers);
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convert numeric to string without scientific notation
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
} 