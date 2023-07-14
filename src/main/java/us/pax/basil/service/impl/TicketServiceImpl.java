package us.pax.basil.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.constant.DropDownConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.ticket.*;
import us.pax.basil.mapper.TicketMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.TicketService;
import us.pax.basil.utils.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import us.pax.basil.security.CustomUserDetails;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import org.springframework.stereotype.Service;
import us.pax.basil.utils.QueryUtils;

import javax.persistence.EntityManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

//    @Override
//    public QueryResultDTO querySerialNumberStatus(String serialNumber){
//        try{
//            Device device = ticketMapper.queryDevice(serialNumber);
//            if(device == null){
//                //if is not U.S. based device.
//                return new QueryResultDTO(null,  -1, "This device is not a U.S. device.");
//            }else{
//                String warrantyStatus = QueryUtils.calculateWarrantyStatus((Date) device.getEndDate(),(Date)device.getVoidDate(),Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
//                if (warrantyStatus == "Out Of Warranty"){
//                    return new QueryResultDTO(null, -1, "This device is out of warranty.");
//                }else {
//                    Device queryDuplicate = ticketMapper.queryDeviceDuplicate(serialNumber);
//                    if(queryDuplicate != null){ //means there is duplicate tickets have this serial number
//                        return new QueryResultDTO(null, -1, "This device is already in another ticket. Please check again");
//                    }
//                    else{
//                        Map<String, Object> result = new HashMap<>();
//                        result.put("voidDate",device.getVoidDate());
//
//                        //check if it is within another ticket. compared with ship date.
//                        return new QueryResultDTO(result, -1, "This device is in another open ticket.");
//                    }
//                }
//            }
//        }catch(Exception e) {
//            return new QueryResultDTO(null, -1, e.getMessage());
//        }
//    }


        /*
const newSerial = {

serialNumber: serialNumber,
model: null,
version: null,
customerReportedIssue: customerReportedIssue,
customerRMA: customerRMA,
terminalID: terminalID,
warrantyExpDate: null,
warrantyStatus: null, (e.g. "Out Of Warranty." | "Within Warranty.")
cosmeticPrice: xxx,
diagnosticPrice: xxx,
minorPrice: xxx,
errorMsg  (e.g. "This device is not a U.S. device."   |   "This device is already in another ticket. Please check again.")
};
* */

    @Override
    public QueryResultArrayDTO batchSerialNumberQuery(EntityManager entityManager, MultipartFile file, String fileName){
        Workbook workbook=null;
        Sheet sheet=null;
        Row row=null;
        fileName = fileName.replaceAll("\\s", "_");
        fileName = fileName.replaceAll(".xlsx", "");

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();//use to store final result and return to front end
        int totalSerialNumber = 0;

        try {
            workbook = WorkbookFactory.create(file.getInputStream());

            for (int i = 0; i < workbook.getNumberOfSheets(); ++i) {
                sheet = workbook.getSheetAt(i);
                totalSerialNumber += sheet.getLastRowNum();
                //get WARRANTY_DATE, WARRANTY_STATUS, and other columns from database here
                for (int j = 1; j <= sheet.getLastRowNum(); ++j) {
                    row = sheet.getRow(j);
                    //dont need another for loop, because I can specify the 3rd and 4th columns into result Array.

                    if (row == null){
                        totalSerialNumber--;
                        break;
                    }

                    String currSerialNumber = QueryUtils.getCellValue(sheet.getRow(j).getCell(0));
                    Device device = ticketMapper.queryDevice(currSerialNumber);//query one serial Number

                    if(device == null){//if is not U.S. based device.

                        Map<String, Object> errorMap = new HashMap<>();
                        errorMap.put("errorMsg","This device is not a U.S. device.");
                        resultArray.add(errorMap);

                    }else{

                        String warrantyStatus = QueryUtils.calculateWarrantyStatus((Date) device.getWarrantyExpDate(),(Date)device.getVoidDate(),Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

                        if (warrantyStatus != "Under Warranty"){

                            Map<String, Object> errorMap = new HashMap<>();
                            errorMap.put("errorMsg",warrantyStatus);
                            resultArray.add(errorMap);

                        }else {

                            warrantyStatus = "Within Warranty.";
                            Device queryDuplicate = ticketMapper.queryDeviceDuplicate(currSerialNumber);

                            if(queryDuplicate != null){ //means there is duplicate tickets have this serial number

                                Map<String, Object> errorMap = new HashMap<>();
                                errorMap.put("errorMsg","This device is already in another ticket. Please check again.");
                                resultArray.add(errorMap);

                            }
                            else{
                                CustomUserDetails user = AuthUtil.getUser();
                                String companyId = String.valueOf(user.getCompanyId());
                                Device getDevice = ticketMapper.queryDeviceInfo(currSerialNumber,companyId);
                                for(int cellIndex = 0; cellIndex<row.getLastCellNum();++cellIndex) {
                                    Map<String, Object> batchDeviceInfo = new HashMap<>();
                                    batchDeviceInfo.put("serialNumber",currSerialNumber);
                                    batchDeviceInfo.put("model",getDevice.getModel());
                                    batchDeviceInfo.put("version",getDevice.getVersion());
                                    batchDeviceInfo.put("customerReportedIssue",QueryUtils.getCellValue(sheet.getRow(j).getCell(1)));
                                    batchDeviceInfo.put("customerRMA",QueryUtils.getCellValue(sheet.getRow(j).getCell(1)));
                                    batchDeviceInfo.put("terminalID",QueryUtils.getCellValue(sheet.getRow(j).getCell(3)));
                                    batchDeviceInfo.put("warrantyExpDate",getDevice.getWarrantyExpDate());
                                    batchDeviceInfo.put("warrantyExpDate",warrantyStatus);
                                    batchDeviceInfo.put("cosmeticPrice",getDevice.getCosmeticPrice());
                                    batchDeviceInfo.put("diagnosticPrice",getDevice.getDiagnosticPrice());
                                    batchDeviceInfo.put("minorPrice",getDevice.getMinorPrice());
                                    batchDeviceInfo.put("errorMsg","");
                                    resultArray.add(batchDeviceInfo);
                                }
                            }
                        }
                    }
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
        return new QueryResultArrayDTO(resultArray,totalSerialNumber,0,"");
    }
}
