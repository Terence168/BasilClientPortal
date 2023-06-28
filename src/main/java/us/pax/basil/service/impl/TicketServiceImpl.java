package us.pax.basil.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import us.pax.basil.constant.DropDownConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.ticket.*;
import us.pax.basil.mapper.TicketMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.TicketService;
import us.pax.basil.utils.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.util.*;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Integer> implements TicketService {

    private TicketMapper ticketMapper;
    @Override
    public QueryResultArrayDTO ticketQuery(Integer currentPage,
                                           Integer sizePerPage,
                                           String sortColumns,
                                           String ticketId,
                                           Integer department,
                                           String responder,
                                           Integer status,
                                           Integer type,
                                           String createdDate,
                                           String serialNumber,
                                           String customerId){
        String[] createdDates;
        String createdFromDate = null;
        String createdToDate = null;

        if(createdDate != null){
            createdDates = createdDate.split("~");
            createdFromDate = createdDates[0];
            createdToDate = createdDates[1];
        }

        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;

        if(user!=null){
            if(user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else
                companyId = customerId;
        }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try{
            Integer total = ticketMapper.getTicketingTotal(companyId, transformInputQuery(ticketId), department, type, status, responder, transformInputQuery(serialNumber), createdFromDate,createdToDate);
            List<TicketingQueue> ticketingQueueList = ticketMapper.getTicketing((currentPage-1) * sizePerPage,
                    sizePerPage,
                    buildSortString(sortColumns),
                    companyId,
                    transformInputQuery(ticketId),
                    department,
                    type,
                    status,
                    responder,
                    transformInputQuery(serialNumber),
                    createdFromDate,
                    createdToDate
                    );

            for(TicketingQueue ticketingQueue: ticketingQueueList){

                Map<String, Object> ticketingQueueMap = new HashMap<>();
                ticketingQueueMap.put("ticketId", ticketingQueue.getTicketId());
                ticketingQueueMap.put("status", ticketingQueue.getStatus());
                ticketingQueueMap.put("department", ticketingQueue.getDepartment());
                ticketingQueueMap.put("type", ticketingQueue.getType());
                ticketingQueueMap.put("createdDate", ticketingQueue.getCreatedDate());
                ticketingQueueMap.put("responder", ticketingQueue.getResponder());
                ticketingQueueMap.put("customer", ticketingQueue.getCustomerOrganization());

                resultArray.add(ticketingQueueMap);
            }
            return new QueryResultArrayDTO(resultArray, total, 0,"");
        }
        catch(Exception e){
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
                case "ticketId":
                    col = col.replace("ticketId", "ticketId");
                    break;
                case "status":
                    col = col.replace("status", "status");
                    break;
                case "department":
                    col = col.replace("department", "department");
                    break;
                case "type":
                    col = col.replace("type", "type");
                    break;
                case "customerOrganization":
                    col = col.replace("customerOrganization", "customer_organization");
                    break;
                case "createdDate":
                    col = col.replace("createdDate", "createdDate");
                    break;
                case "responder":
                    col = col.replace("responder", "responder");
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
    public QueryResultArrayDTO queryDepartment(){
        try{
            List<Department> departmentList = ticketMapper.queryDepartmentList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (Department department: departmentList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();

                mm.put(DropDownConstant.DROPDOWN_VALUE, department.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, department.getDepartment());

                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        }catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    @Override
    public QueryResultArrayDTO queryOrderType(){
        try{
            List<OrderType> orderTypeList = ticketMapper.queryOrderTypeList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (OrderType orderType: orderTypeList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();

                mm.put(DropDownConstant.DROPDOWN_VALUE, orderType.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, orderType.getOrderType());

                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        }catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    @Override
    public QueryResultArrayDTO queryStatus(){
        try{
            List<Status> statusList = ticketMapper.queryStatusList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (Status status: statusList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();

                mm.put(DropDownConstant.DROPDOWN_VALUE, status.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, status.getStatus());

                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        }catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
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
    public QueryResultArrayDTO serialNumberStatus(String serialNumber){
        try{
            Device isUSBased = ticketMapper.queryDeviceBase(serialNumber);
            if(isUSBased == null){
                //if is not U.S. based device.
                return new QueryResultArrayDTO(null, 0, -1, "This serial number shows the device is not a U.S. device.");
            }else{
                //todo
                return new QueryResultArrayDTO(null, 0, -1, "This serial number shows the device is not a U.S. device.");
            }
        }catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
}
