package us.pax.basil.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.constant.DropDownConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.ticket.*;
import us.pax.basil.mapper.TicketMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.TicketService;
import us.pax.basil.utils.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;
import java.util.*;
import org.springframework.stereotype.Service;
import us.pax.basil.utils.QueryUtils;
import javax.persistence.EntityManager;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


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

    @Override
    public QueryResultArrayDTO ticketQueryViews(Integer currentPage, Integer sizePerPage, String sortColumns, String ticketId, Integer department, String responder, Integer status, Integer type, String createdDate, String lastResponse, String serialNumber, String customerOrganization,String customerId) {
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
            Integer total = ticketMapper.getTicketingViewsTotal(companyId, transformInputQuery(ticketId), department, type, status, responder, transformInputQuery(serialNumber), createdFromDate,createdToDate,lastResponse,customerOrganization,customerId);
            List<TicketView> ticketingQueueList = ticketMapper.getTicketingViews((currentPage-1) * sizePerPage,
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
                    createdToDate,
                    lastResponse,
                    customerOrganization,
                    customerId

            );

            for(TicketView ticketingviews: ticketingQueueList){

                Map<String, Object> ticketingViewsMap = new HashMap<>();
                ticketingViewsMap.put("ticketId", ticketingviews.getTicketId());
                ticketingViewsMap.put("status", ticketingviews.getStatus());
                ticketingViewsMap.put("department", ticketingviews.getDepartment());
                ticketingViewsMap.put("type", ticketingviews.getType());
                ticketingViewsMap.put("createdDate", ticketingviews.getCreatedDate());
                ticketingViewsMap.put("responder", ticketingviews.getResponder());
                ticketingViewsMap.put("lastResponse", ticketingviews.getLastResponse());
                ticketingViewsMap.put("customer", ticketingviews.getCustomerOrganization());

                resultArray.add(ticketingViewsMap);
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
    @Override
    public QueryResultArrayDTO queryRepairType(){
        try{
            List<RepairType> repairTypeList = ticketMapper.queryRepairTypeList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (RepairType rt: repairTypeList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();
                mm.put(DropDownConstant.DROPDOWN_VALUE, rt.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, rt.getRepairType());
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
    public QueryResultArrayDTO batchSerialNumberQuery(EntityManager entityManager, MultipartFile file, String fileName){
        Workbook workbook=null;
        Sheet sheet=null;
        Row row=null;
        fileName = fileName.replaceAll("\\s", "_");
        fileName = fileName.replaceAll(".xlsx", "");
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();//use to store final result and return to front end
        int totalSerialNumber = 0;
        List<String> serialNumbersInFile = new ArrayList<>();
        HashMap<String,String[]> deviceInfoMap = new HashMap<>();

        try {
            workbook = WorkbookFactory.create(file.getInputStream());
            for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
                sheet = workbook.getSheetAt(i);
                totalSerialNumber += sheet.getLastRowNum();
                //get WARRANTY_DATE, WARRANTY_STATUS, and other columns from database here
                for (int j = 1; j <= sheet.getLastRowNum(); ++j) {
                    row = sheet.getRow(j);
                    if (row == null) {
                        totalSerialNumber--;
                        break;
                    }
                    String currSerialNumber = QueryUtils.getCellValue(sheet.getRow(j).getCell(0));
                    serialNumbersInFile.add(currSerialNumber);
                    String[] temp = new String[3];
                    temp[0] = QueryUtils.getCellValue(sheet.getRow(j).getCell(1)); //customer reported issue
                    temp[1] = QueryUtils.getCellValue(sheet.getRow(j).getCell(2)); // customer RMA
                    temp[2] = QueryUtils.getCellValue(sheet.getRow(j).getCell(3)); //terminalID
                    deviceInfoMap.put(currSerialNumber,temp);
                }
            }
        }catch(Exception e){
            String msg = e.getMessage();
            if (sheet != null && row != null)
                msg = msg +  " sheet: " + sheet.getSheetName() + ", row: " + row.getRowNum();
            return new QueryResultArrayDTO(null,0,-1,msg);
        }finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (IOException e) {
                return new QueryResultArrayDTO(null,0,-1,e.getMessage());
            }
        }

        resultArray = getResult(serialNumbersInFile, deviceInfoMap);
        return new QueryResultArrayDTO(resultArray,totalSerialNumber,0,"");
    }

    private ArrayList<Map<String, Object>> getResult(List<String> serialNumberList, HashMap<String,String[]> deviceInfoMap){
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<String> usBasedDevices = ticketMapper.findUSBasedDevices(serialNumberList);
        List<String> notUSBasedDevices = new ArrayList<>();
        for(String s: serialNumberList){
            if(!usBasedDevices.contains(s)){
                notUSBasedDevices.add(s);
            }
        }

        for(String nus: notUSBasedDevices){
            Map<String, Object> batchDeviceInfo = new HashMap<>();
            batchDeviceInfo.put("serialNumber", nus);
            batchDeviceInfo.put("errorMsg","This is not a U.S.Device or you input the wrong Serial Number.");
            resultArray.add(batchDeviceInfo);
        }
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = String.valueOf(user.getCompanyId());
        List<Device> getDevice = ticketMapper.getDeviceInfos(usBasedDevices,companyId);
        for(Device d : getDevice){
            Map<String, Object> batchDeviceInfo = new HashMap<>();
            String errorMsg = "";
            String curSN = d.getSerialNumber();
            batchDeviceInfo.put("serialNumber",curSN);
            batchDeviceInfo.put("xm_OID",d.getXmOID());
            batchDeviceInfo.put("msn_OID",d.getMsnOID());
            batchDeviceInfo.put("model",d.getModel());
            batchDeviceInfo.put("version",d.getVersion());
            batchDeviceInfo.put("customerReportedIssue",deviceInfoMap.get(curSN)[0]);
            batchDeviceInfo.put("customerRMA",deviceInfoMap.get(curSN)[1]);
            batchDeviceInfo.put("terminalID",deviceInfoMap.get(curSN)[2]);
            batchDeviceInfo.put("warrantyExpDate",d.getWarrantyExpDate());
            batchDeviceInfo.put("warrantyStatus",d.getWarrantyStatus());
            if(!d.getWarrantyStatus().equals("Under Warranty"))
                errorMsg = d.getWarrantyStatus();
            batchDeviceInfo.put("cosmeticPrice",d.getCosmeticPrice());
            batchDeviceInfo.put("diagnosticPrice",d.getDiagnosticPrice());
            batchDeviceInfo.put("minorPrice",d.getMinorPrice());
            batchDeviceInfo.put("existInAnotherTicket",d.getExistInAnotherTicket());
            if(d.getExistInAnotherTicket()==true)
                errorMsg = "This device has already existed in another active ticket.";
            batchDeviceInfo.put("errorMsg",errorMsg);
            resultArray.add(batchDeviceInfo);
        }
        return resultArray;
    }
    @Override
    public QueryResultArrayDTO serialNumberQuery(String serialNumber){
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>(); //use to store final result and return to front end
        List<String> serialNumberList = new ArrayList<>();
        HashMap<String,String[]> deviceInfoMap = new HashMap<>();
        serialNumberList.add(serialNumber);
        String[] temp = new String[3];
        temp[0] = ""; // customer reported issue, there is no need to pass these parameters when query device information, only when submitting ticket. These information will be stored in database.
        temp[1] = ""; // customer RMA
        temp[2] = ""; // terminalID
        deviceInfoMap.put(serialNumber,temp);
        resultArray = getResult(serialNumberList,deviceInfoMap);
        return new QueryResultArrayDTO(resultArray,1,0,"");
    }


    @Override
    public int insertTicketToPMO(TicketInsertionObject tio){ // PMO is prep_master_order
        CustomUserDetails user = AuthUtil.getUser();
        Integer companyId = user.getCompanyId();
        tio.setMcOID(companyId);
        tio.setOrderStatus("12");
        tio.setOrderDateToCurrentDate();
        ticketMapper.insertPrep_Master_Order(tio);
        return tio.getMoOID();
    }

    @Override
    public SqlResultDTO submitTicket(List<SNsInsertionObject> sNsInsertionObjectList) {
        TicketInsertionObject tio = new TicketInsertionObject();
        int mo_OID = insertTicketToPMO(tio);
        for (SNsInsertionObject snsObject : sNsInsertionObjectList) {
            snsObject.setMoOID(mo_OID);
        }
        try {
            ticketMapper.insertPrep_Xref_Materials(sNsInsertionObjectList);
            return new SqlResultDTO(0, "");
        } catch (Exception e) {
            return new SqlResultDTO(-1, e.getMessage());
        }
    }
}
