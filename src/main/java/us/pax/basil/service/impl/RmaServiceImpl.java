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
import us.pax.basil.entity.rma.*;
import us.pax.basil.mapper.RmaMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.RmaService;
import us.pax.basil.utils.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
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
    public QueryResultArrayDTO statusQuery(Integer currentPage, Integer sizePerPage, String sortColumns, String rmaNumber,
            String serialNumber, String partNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryResultArrayDTO shippedQuery(Integer currentPage, 
                                             Integer sizePerPage, 
                                             String sortColumns,
                                             String rmaNumber,
                                             String serialNumber, 
                                             String partNumber,
                                             String shipDate,
                                             String customerId) {
        String [] shipDates;
        String shipFromDate = null;
        String shipToDate = null;

        if (shipDate != null) {
            shipDates = shipDate.split(" ~ ");
            shipFromDate = shipDates[0];
            shipToDate = shipDates[1];
        }

        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;
        
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
           		companyId = customerId;
            }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try {
            Integer total = rmaMapper.getShippingTotal(companyId, transformInputQuery(rmaNumber),
                    transformInputQuery(serialNumber), partNumber, shipFromDate,
                    shipToDate);
    
            List<Shipped> shippedList = rmaMapper.getShipping((currentPage - 1) * sizePerPage,
                                                              sizePerPage, 
                                                              buildSortString(sortColumns),
                                                              companyId,
                                                              transformInputQuery(rmaNumber),
                                                              transformInputQuery(serialNumber),
                                                              partNumber,
                                                              shipFromDate,
                                                              shipToDate);

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
                shippedMap.put("customerOrganization", shipped.getCustomerOrganization());
    
                resultArray.add(shippedMap);
            }
            return new QueryResultArrayDTO(resultArray, total, 0, "");
        } catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    @Override
    public ArrayList<Shipped> shippedExcelExportQuery(String sortColumns,
                                                      String rmaNumber,
                                                      String serialNumber,
                                                      String partNumber,
                                                      String shipDate,
                                                      String customerId) {
        String [] shipDates;
        String shipFromDate = null;
        String shipToDate = null;

        if (shipDate != null) {
            shipDates = shipDate.split(" ~ ");
            shipFromDate = shipDates[0];
            shipToDate = shipDates[1];
        }

        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;

        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
                companyId = customerId;
            }

        return (ArrayList<Shipped>) rmaMapper.getShipping(0,
                100000,
                buildSortString(sortColumns),
                companyId,
                transformInputQuery(rmaNumber),
                transformInputQuery(serialNumber),
                partNumber,
                shipFromDate,
                shipToDate);
    }

    @Override
    public QueryResultArrayDTO quarantineQuery(Integer currentPage, 
                                                Integer sizePerPage, 
                                                String sortColumns, 
                                                String rmaNumber,
                                                String serialNumber,
                                                String model,
                                                String customerId,
                                                Integer contact) {
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
           		companyId = customerId;
            }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try {
            Integer total = rmaMapper.getQuarantineTotal(companyId, transformInputQuery(rmaNumber),
                    transformInputQuery(serialNumber), model, contact);
    
            List<Quarantine> quarantineList = rmaMapper.getQuarantine((currentPage - 1) * sizePerPage,
                                                                      sizePerPage, 
                                                                      buildSortString(sortColumns),
                                                                      companyId,
                                                                      transformInputQuery(rmaNumber),
                                                                      transformInputQuery(serialNumber),
                                                                      model,
                                                                      contact);

            for (Quarantine quarantine: quarantineList) { 
                Map<String, Object> quarantineMap = new HashMap<>();
    
                quarantineMap.put("quarantineDate", quarantine.getQuarantineDate());
                quarantineMap.put("partNumber", quarantine.getPartNumber());
                quarantineMap.put("serialNumber", quarantine.getSerialNumber());
                quarantineMap.put("rmaNumber", quarantine.getRmaNumber());
                quarantineMap.put("customerContact", quarantine.getCustomerContact());
                quarantineMap.put("techNotes", quarantine.getTechNotes());
                quarantineMap.put("faultCode", quarantine.getFaultCode());
                quarantineMap.put("partsNeeded", quarantine.getPartsNeeded());
                quarantineMap.put("customerOrganization", quarantine.getCustomerOrganization());
    
                resultArray.add(quarantineMap);
            }
            return new QueryResultArrayDTO(resultArray, total, 0, "");
        } catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    @Override
    public ArrayList<Quarantine> quarantineExcelExportQuery(String sortColumns,
                                                            String rmaNumber,
                                                            String serialNumber,
                                                            String model,
                                                            String customerId,
                                                            Integer contact) {
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
                companyId = customerId;
            }
        return (ArrayList<Quarantine>) rmaMapper.getQuarantine(0,
                100000,
                buildSortString(sortColumns),
                companyId,
                transformInputQuery(rmaNumber),
                transformInputQuery(serialNumber),
                model,
                contact);
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
                case "quarantineDate":
                    col = col.replace("quarantineDate", "quarantineDate");
                    break;
                case "shipDate":
                    col = col.replace("shipDate", "shipDate");
                    break;
                case "customerContact":
                    col = col.replace("customerContact", "customerContact");
                    break;
                case "customerOrganization":
                    col = col.replace("customerOrganization", "customer_organization");
                    break;
                case "techNotes":
                    col = col.replace("techNotes", "techNotes");
                    break;
                case "trackingNumber":
                    col = col.replace("trackingNumber", "trackingNumber");
                    break;
                case "reportedIssue":
                    col = col.replace("reportedIssue", "reportedIssue");
                    break;
                case "faultCode":
                    col = col.replace("faultCode", "faultCode");
                    break;
                case "partsNeeded":
                    col = col.replace("partsNeeded", "partsNeeded");
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

    private String[] transformInputQuery(String query) {
        String[] output = null;

        if (query != null) {
            output = query.split(",");
            for (int i = 0; i < output.length; i++) {
                output[i] = output[i].trim();
            }
        }

        return output;
    }

	@Override
	public QueryResultArrayDTO statusTier1(String partNumber, String rmaNumber, String serialNumber, String customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;
        
        if (user != null) {
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
            	companyId = customerId;
            }
        }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<PartNumberTier1> partNumberList = rmaMapper.getPartNumberTier1(companyId,
                partNumber, transformInputQuery(rmaNumber), transformInputQuery(serialNumber));
        Collections.sort(partNumberList, (o1, o2) -> (o1.getPartNumber().compareTo(o2.getPartNumber())));
        try {
        	for (PartNumberTier1 part: partNumberList) {
        		Map<String, Object> result = new HashMap<>();

		    	result.put("partNumber", part.getPartNumber());
		    	result.put("inventory", part.getInventory());
		    	result.put("outForRepair", part.getOutForRepair());
		    	result.put("quarantine", part.getQuarantine());
    			result.put("awaitingQaCa", part.getAwaitingQaCa());
    			result.put("screening", part.getScreening());
    			result.put("readyToShip", part.getReadyToShip());
    			result.put("total", part.getTotal());
    			result.put("customerOrganization", part.getCustomerOrganization());
    			
    			resultArray.add(result);
        	}

        	return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        }catch(Exception e) {
        	return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}

	@Override
	public QueryResultArrayDTO statusTier2(String partNumber, String rmaNumber, String serialNumber, String customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;
        
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
            	companyId = customerId;
            }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<RmaNumberTier2> rmaNumberList = rmaMapper.getRmaNumberTier2(companyId, partNumber,
                transformInputQuery(rmaNumber), transformInputQuery(serialNumber));
        try {
        	for (RmaNumberTier2 rma: rmaNumberList) {
        		Map<String, Object> result = new HashMap<>();

		    	result.put("rmaNumber", rma.getRmaNumber());
		    	result.put("inventory", rma.getInventory());
		    	result.put("outForRepair", rma.getOutForRepair());
		    	result.put("quarantine", rma.getQuarantine());
    			result.put("awaitingQaCa", rma.getAwaitingQaCa());
    			result.put("screening", rma.getScreening());
    			result.put("readyToShip", rma.getReadyToShip());
    			result.put("total", rma.getTotal());
    			result.put("customerOrganization", rma.getCustomerOrganization());
    			
    			resultArray.add(result);
        	}

        	return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        }catch(Exception e) {
        	return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}

	@Override
	public QueryResultArrayDTO statusTier3(String rmaNumber, String partNumber, String serialNumberFilter, String customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;
        
        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
           		companyId = customerId;
            }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<SerialNumberTier3> serialNumberList = rmaMapper.getSerialNumberTier3(companyId,
                rmaNumber, partNumber, transformInputQuery(serialNumberFilter));
        try {
        	for (SerialNumberTier3 serialNumber: serialNumberList) {
        		Map<String, Object> result = new HashMap<>();

                result.put("id", serialNumber.getId());
                result.put("serialNumber", serialNumber.getSerialNumber());
		    	result.put("inventory", serialNumber.getInventory());
		    	result.put("outForRepair", serialNumber.getOutForRepair());
		    	result.put("quarantine", serialNumber.getQuarantine());
    			result.put("awaitingQaCa", serialNumber.getAwaitingQaCa());
    			result.put("screening", serialNumber.getScreening());
    			result.put("readyToShip", serialNumber.getReadyToShip());
    			result.put("customerOrganization", serialNumber.getCustomerOrganization());
    			
    			resultArray.add(result);
        	}

        	return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        }catch(Exception e) {
        	return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}

    public QueryResultArrayDTO statusTier4(Integer id) {
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        SerialDetailsTier4 serialDetails = rmaMapper.getSerialDetailsTier4(id);
        try {
            if (serialDetails == null) {
                return new QueryResultArrayDTO(null, 0, 0, "");
            }

            Map<String, Object> result = new HashMap<>();

            result.put("reportedIssue", serialDetails.getReportedIssue());
            result.put("faultCodes", serialDetails.getFaultCodes());
            result.put("customer", serialDetails.getCustomerOrganization());
            result.put("receivedDate", serialDetails.getReceivedDate());

            resultArray.add(result);

            return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        } catch(Exception e) {
            e.printStackTrace();
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    @Override
    public ArrayList<StatusExcelExport> statusExcelExportQuery(String partNumber,
                                                               String rmaNumber,
                                                               String serialNumber,
                                                               String customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;

        if (user != null)
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else {
                companyId = customerId;
            }

        return rmaMapper.statusExcelExport(companyId,
                partNumber, transformInputQuery(rmaNumber), transformInputQuery(serialNumber));
    }
}
