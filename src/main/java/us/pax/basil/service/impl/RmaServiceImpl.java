package us.pax.basil.service.impl;


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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.rma.PartNumberTier1;
import us.pax.basil.entity.rma.Quarantine;
import us.pax.basil.entity.rma.RmaNumberTier2;
import us.pax.basil.entity.rma.SerialNumberTier3;
import us.pax.basil.entity.rma.Shipped;
import us.pax.basil.mapper.RmaMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.RmaService;
import us.pax.basil.utils.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class RmaServiceImpl extends ServiceImpl<RmaMapper, Integer> implements RmaService {
    private RmaMapper rmaMapper;

    @Override
    public QueryResultArrayDTO statusQuery(Integer currentPage, Integer sizePerPage, String sortColumns, Long rmaNumber,
            String serialNumber, String partNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResultArrayDTO shippedQuery(Integer currentPage, 
                                             Integer sizePerPage, 
                                             String sortColumns, 
                                             Long rmaNumber, 
                                             String serialNumber, 
                                             String partNumber,
                                             Integer customerId) {
        /*
           String [] receivedDates = {null,null};
           if (dateReceived != null)
               receivedDates = DateTimeUtil.getStartEnd(dateReceived, DateTimeUtil.PATTERN_YYYYMMDD_WITH_SLASH, "~");
        */
        CustomUserDetails user = AuthUtil.getUser();
        Integer companyId = null;
        
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = user.getCompanyId();
            else
                companyId = customerId;

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try {
            Integer total = rmaMapper.getShippingTotal(companyId, rmaNumber, serialNumber, partNumber);
    
            List<Shipped> shippedList = rmaMapper.getShipping((currentPage - 1) * sizePerPage,
                                                              sizePerPage, 
                                                              buildSortString(sortColumns),
                                                              companyId,
                                                              rmaNumber, 
                                                              serialNumber, 
                                                              partNumber);

            for (Shipped shipped: shippedList) { 
                Map<String, Object> shippedMap = new HashMap<>();
    
                shippedMap.put("shipDate", shipped.getShipDate());
                shippedMap.put("partNumber", shipped.getPartNumber());
                shippedMap.put("serialNumber", shipped.getSerialNumber());
                shippedMap.put("rmaNumber", shipped.getRmaNumber());
                shippedMap.put("trackingNumber", shipped.getTrackingNumber());
                shippedMap.put("reportedIssue", shipped.getReportedIssue());
                shippedMap.put("techNotes", shipped.getTechNotes());
                shippedMap.put("faultCode", shipped.getFaultCode());
    
                resultArray.add(shippedMap);
            }
            return new QueryResultArrayDTO(resultArray, total, 0, "");
        } catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    @Override
    public QueryResultArrayDTO quarantineQuery(Integer currentPage, 
                                                Integer sizePerPage, 
                                                String sortColumns, 
                                                Long rmaNumber, 
                                                String serialNumber, 
                                                String model,
                                                Integer customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        Integer companyId = null;
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = user.getCompanyId();
            else
                companyId = customerId;

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try {
            Integer total = rmaMapper.getQuarantineTotal(companyId, rmaNumber, serialNumber, model);
    
            List<Quarantine> quarantineList = rmaMapper.getQuarantine((currentPage - 1) * sizePerPage,
                                                                      sizePerPage, 
                                                                      buildSortString(sortColumns),
                                                                      companyId,
                                                                      rmaNumber, 
                                                                      serialNumber, 
                                                                      model);

            for (Quarantine quarantine: quarantineList) { 
                Map<String, Object> quarantineMap = new HashMap<>();
    
                quarantineMap.put("quarantineDate", quarantine.getQuarantineDate());
                quarantineMap.put("partNumber", quarantine.getPartNumber());
                quarantineMap.put("serialNumber", quarantine.getSerialNumber());
                quarantineMap.put("rmaNumber", quarantine.getRmaNumber());
                quarantineMap.put("customerContact", quarantine.getCustomerContact());
                quarantineMap.put("techNotes", quarantine.getTechNotes());
                quarantineMap.put("faultCode", quarantine.getFaultCode());
    
                resultArray.add(quarantineMap);
            }
            return new QueryResultArrayDTO(resultArray, total, 0, "");
        } catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    private String buildSortString(String sortColumns) {
        if (null == sortColumns) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String[] sortCols = sortColumns.split(",");
        for (String col : sortCols) {
            String[] fields = col.split("\\.");
            if (fields.length > 2) {
                log.warn("Ignoring invalid sort field: {}", col);
                continue;
            }

            if (fields.length == 2) {
                if (fields[1].equalsIgnoreCase("asc") || fields[1].equalsIgnoreCase("desc")) {
                    col = col.replace(".", " ");
                } else {
                    log.warn("Ignoring invalid sort field: {}", col);
                    continue;
                }
            }

            switch (fields[0]) {
                case "rmaNumber":
                    col = col.replace("rmaNumber", "rmaNumber");
                    break;
                case "serialNumber":
                    col = col.replace("serialNumber", "serialNumber");
                    break;
                case "partNumber":
                    col = col.replace("partNumber", "partNumber");
                    break;
                default:
                    log.warn("Ignoring invalid sort field: {}", col);
                    continue;
            }

            sb.append(col).append(",");
        }

        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1); // remove the comma at the end of the sort string
        }
        log.info("Sorting String: [{}]", sb.toString());
        return sb.toString();
    }

	@Override
	public QueryResultArrayDTO statusTier1(String partNumber, Long rmaNumber, String serialNumber, Integer customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        Integer companyId = null;
        
        if (user != null) {
            if(user.getStandardUser() == 1)
                companyId = user.getCompanyId();
            else
                companyId = customerId;
        }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<PartNumberTier1> partNumberList = rmaMapper.getPartNumberTier1(companyId, partNumber, rmaNumber, serialNumber);
        try {
        	for (PartNumberTier1 part: partNumberList) {
        		Map<String, Object> result = new HashMap<>();

		    	result.put("partNumber", part.getPartNumber());
		    	result.put("inventory", part.getInventory());
		    	result.put("outForRepair", part.getOutForRepair());
		    	result.put("quarantine", part.getQuarantine());
    			result.put("awaitingQaCa", part.getAwaitingQaCa());
    			result.put("readyToShip", part.getReadyToShip());
    			result.put("total", part.getTotal());
    			
    			resultArray.add(result);
        	}

        	return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        }catch(Exception e) {
        	return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}

	@Override
	public QueryResultArrayDTO statusTier2(String partNumber, Long rmaNumber, String serialNumber, Integer customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        Integer companyId = null;
        
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = user.getCompanyId();
            else
                companyId = customerId;

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<RmaNumberTier2> rmaNumberList = rmaMapper.getRmaNumberTier2(companyId, partNumber, rmaNumber, serialNumber);
        try {
        	for (RmaNumberTier2 rma: rmaNumberList) {
        		Map<String, Object> result = new HashMap<>();

		    	result.put("rmaNumber", rma.getRmaNumber());
		    	result.put("inventory", rma.getInventory());
		    	result.put("outForRepair", rma.getOutForRepair());
		    	result.put("quarantine", rma.getQuarantine());
    			result.put("awaitingQaCa", rma.getAwaitingQaCa());
    			result.put("readyToShip", rma.getReadyToShip());
    			result.put("total", rma.getTotal());
    			
    			resultArray.add(result);
        	}

        	return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        }catch(Exception e) {
        	return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}

	@Override
	public QueryResultArrayDTO statusTier3(Long rmaNumber, String partNumber, String serialNumberFilter, Integer customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        Integer companyId = null;
        
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = user.getCompanyId();
            else
                companyId = customerId;

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<SerialNumberTier3> serialNumberList = rmaMapper.getSerialNumberTier3(companyId, rmaNumber, partNumber, serialNumberFilter);
        try {
        	for (SerialNumberTier3 serialNumber: serialNumberList) {
        		Map<String, Object> result = new HashMap<>();

		    	result.put("serialNumber", serialNumber.getSerialNumber());
		    	result.put("inventory", serialNumber.getInventory());
		    	result.put("outForRepair", serialNumber.getOutForRepair());
		    	result.put("quarantine", serialNumber.getQuarantine());
    			result.put("awaitingQaCa", serialNumber.getAwaitingQaCa());
    			result.put("readyToShip", serialNumber.getReadyToShip());
    			
    			resultArray.add(result);
        	}

        	return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        }catch(Exception e) {
        	return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}
}
