package us.pax.basil.service;

import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.warranty.WarrantyCheck;

import java.io.IOException;
import java.util.List;

public interface WarrantyService {
    
    /**
     * Check warranty information for a single serial number
     * @param serialNumber The serial number to check
     * @return QueryResultArrayDTO containing warranty information
     */
    QueryResultArrayDTO checkWarrantyBySerialNumber(String serialNumber);
    
    /**
     * Check warranty information for multiple serial numbers
     * @param serialNumbers List of serial numbers to check
     * @return QueryResultArrayDTO containing warranty information for all serial numbers
     */
    QueryResultArrayDTO checkWarrantyBatch(List<String> serialNumbers);
    
    /**
     * Extract serial numbers from uploaded file
     * @param file The uploaded file (Excel format)
     * @return List of serial numbers extracted from the file
     * @throws IOException if there's an error reading the file
     */
    List<String> extractSerialNumbersFromFile(MultipartFile file) throws IOException;
    
    /**
     * Get warranty data for export
     * @param serialNumbers List of serial numbers (optional)
     * @return List of WarrantyCheck objects
     */
    List<WarrantyCheck> getWarrantyDataForExport(List<String> serialNumbers);
} 