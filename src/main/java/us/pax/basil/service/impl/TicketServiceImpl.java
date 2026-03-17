package us.pax.basil.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.constant.DropDownConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.entity.customer.Address;
import us.pax.basil.entity.customer.Company;
import us.pax.basil.entity.customer.Customer;
import us.pax.basil.entity.ticket.*;
import us.pax.basil.mapper.LogProgramMapper;
import us.pax.basil.mapper.RmaFileStorageMapper;
import us.pax.basil.mapper.TicketMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.AddressService;
import us.pax.basil.service.InvoiceService;
import us.pax.basil.service.TicketService;
import us.pax.basil.service.aws.s3.RmaAttachmentStorageService;
import us.pax.basil.service.aws.ses.EmailService;
import us.pax.basil.utils.AuthUtil;
import us.pax.basil.utils.LoggingUtil;
import us.pax.basil.utils.QueryUtils;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Log4j2
@Service
@AllArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Integer> implements TicketService {
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TicketMapper ticketMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private RmaFileStorageMapper rmaFileStorageMapper;

    @Autowired
    private RmaAttachmentStorageService rmaAttachmentStorageService;

    @Autowired
    private LogProgramMapper logProgramMapper;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 文档类型扩展名集合（用于后端二次校验，防止绕过前端）。
     */
    private static final Set<String> DOCUMENT_EXTENSIONS = new HashSet<>(Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "csv", "txt"
    ));

    /**
     * 图片类型扩展名集合。
     */
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif"
    ));

    /**
     * 视频类型扩展名集合。
     */
    private static final Set<String> VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList(
            "mp4", "mov", "avi"
    ));

    /**
     * 文档/图片大小限制（10MB）。
     */
    private static final long MAX_DOC_IMAGE_SIZE = 10L * 1024L * 1024L;

    /**
     * 视频大小限制（500MB）。
     */
    private static final long MAX_VIDEO_SIZE = 500L * 1024L * 1024L;

    /**
     * Contact RMA 固定收件邮箱。
     */
    private static final String CONTACT_RMA_EMAIL = "RMAsupport@pax.us";

    /**
     * Contact RMA 截图允许扩展名。
     */
    private static final Set<String> CONTACT_RMA_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif"
    ));
    
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
                                           String customerId) {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        
        if (user.isClientUser()) {
            return new QueryResultArrayDTO(null, 0, -1, "Don't have access to the ticket");
        }
        String[] createdDates;
        String createdFromDate = null;
        String createdToDate = null;
        
        if (createdDate != null) {
            createdDates = createdDate.split("~");
            createdFromDate = createdDates[0];
            createdToDate = createdDates[1];
        }
        
        String companyId = null;
        
        if (user != null) {
            if (user.getStandardUser() == 1)
                return new QueryResultArrayDTO(null, 0, -1, "You are not authorized to access this resource");
            else
                companyId = customerId;
        }
        
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try {
            Integer total = ticketMapper.getTicketingTotal(companyId, transformInputQuery(ticketId), department, type, status, responder, transformInputQuery(serialNumber), createdFromDate, createdToDate);
            List<TicketingQueue> ticketingQueueList = ticketMapper.getTicketing((currentPage - 1) * sizePerPage,
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
            
            for (TicketingQueue ticketingQueue : ticketingQueueList) {
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
            return new QueryResultArrayDTO(resultArray, total, 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    
    @Override
    public QueryResultArrayDTO ticketQueryViews(Integer currentPage, Integer sizePerPage, String sortColumns, String ticketId, Integer department, String responder, Integer status, Integer type, String createdDate, String lastResponse, String serialNumber, String customerOrganization, String customerId) {
        String[] createdDates;
        String createdFromDate = null;
        String createdToDate = null;
        
        if (createdDate != null) {
            createdDates = createdDate.split("~");
            createdFromDate = createdDates[0];
            createdToDate = createdDates[1];
        }
        
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = null;
        
        if (user != null) {
            if (user.getStandardUser() == 1)
                companyId = String.valueOf(user.getCompanyId());
            else
                companyId = customerId;
        }
        
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try {
            Integer total = ticketMapper.getTicketingViewsTotal(companyId, transformInputQuery(ticketId), department, type, status, responder, transformInputQuery(serialNumber), createdFromDate, createdToDate, lastResponse, customerOrganization, customerId);
            List<TicketView> ticketingViewsList = ticketMapper.getTicketingViews((currentPage - 1) * sizePerPage,
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
            
            if (!ticketingViewsList.isEmpty()) {
                for (TicketView ticketingviews : ticketingViewsList) {
                    
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
            }
            return new QueryResultArrayDTO(resultArray, total, 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO viewTicketDetails(Integer id, Integer ticketId) {
        if (!userHasAccess(String.valueOf(ticketId))) {
            return new QueryResultArrayDTO(null, 0, -1, "Don't have access to the ticket");
        }
        try {
            ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
            List<RepairRecord> repairRecords = ticketMapper.getRepairDetail(id);
            if (repairRecords.isEmpty()) {
                repairRecords = ticketMapper.getPrepRepairDetail(id);
            }
            
            for (RepairRecord repairRecord : repairRecords) {
                //repairRecord.setWarrantyStatus(QueryUtils.calculateWarrantyStatus(repairRecord.getWarrantyEndDate(), repairRecord.getWarrantyVoidedDate(), repairRecord.getOrderDate()));
                Map<String, Object> objectMap = objectMapper.convertValue(repairRecord, Map.class);
                resultArray.add(objectMap);
            }
            
            return new QueryResultArrayDTO(resultArray, repairRecords.size(), 0, null);
        } catch (Exception e) {
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
    public QueryResultArrayDTO queryDepartment() {
        try {
            List<Department> departmentList = ticketMapper.queryDepartmentList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (Department department : departmentList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();
                
                mm.put(DropDownConstant.DROPDOWN_VALUE, department.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, department.getDepartment());
                
                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryOrderType() {
        try {
            List<OrderType> orderTypeList = ticketMapper.queryOrderTypeList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (OrderType orderType : orderTypeList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();
                
                mm.put(DropDownConstant.DROPDOWN_VALUE, orderType.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, orderType.getOrderType());
                
                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryStatus() {
        try {
            List<Status> statusList = ticketMapper.queryStatusList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (Status status : statusList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();
                mm.put(DropDownConstant.DROPDOWN_VALUE, status.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, status.getStatus());
                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryRepairType() {
        try {
            List<RepairType> repairTypeList = ticketMapper.queryRepairTypeList();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            for (RepairType rt : repairTypeList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();
                mm.put(DropDownConstant.DROPDOWN_VALUE, rt.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, rt.getRepairType());
                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    
    public QueryResultDTO viewEditTicket(String id) {
        if (!userHasAccess(id)) {
            return new QueryResultDTO(null, -1, "Don't have access to the ticket");
        }
        try {
            TicketInfo ticket = ticketMapper.existingMasterOrder(id); //existing check BASIL_ODS_PRD.XREF_MATERIALS\
            Integer companyId;
            if (ticket == null) {
                ticket = ticketMapper.existingPREPMasterOrder(id); //existing check BASIL_SEC_PRD.PREP_XREF_MATERIALS
                if (ticket != null) {
                    ticket.setIsFromMaster(false);
                    companyId = ticket.getMcOID();
                    if (ticket.getEncrypt() == null) {
                        ticket.setEncrypt("no");
                    }
                    
                    if (ticket.getKeyType() == null || ticket.getKeyType().length() == 0) {
                        ticket.setKeyType("N/A");
                    }
                    
                    if (ticket.getKcv() == null || ticket.getKcv().length() == 0) {
                        ticket.setKcv("N/A");
                    }
                    
                    if (ticket.getKsi() == null || ticket.getKsi().length() == 0) {
                        ticket.setKsi("N/A");
                    }
                    
                    List<SNInfo> prefDevices = ticketMapper.getSecMaterials(id, companyId);
                    List<SNInfo> xrefDevices = ticketMapper.getOdsMaterials(id, companyId);
                    
                    Map<String, SNInfo> devicesMap = new HashMap<>();
                    for (SNInfo prefDevice : prefDevices) {
                        devicesMap.put(prefDevice.getSerialNumber(), prefDevice);
                    }
                    for (SNInfo xrefDevice : xrefDevices) {
                        devicesMap.put(xrefDevice.getSerialNumber(), xrefDevice);
                    }
                    
                    List<SNInfo> serials = devicesMap.entrySet().stream().map((e) -> e.getValue()).collect(Collectors.toList());
                    ticket.setSerials(serials);
                }
            } else {
                ticket.setIsFromMaster(true);
                companyId = ticket.getMcOID();
                List<SNInfo> devices = ticketMapper.getOdsMaterials(id, companyId);
                ticket.setSerials(devices);
            }
            
            if (ticket != null) {
                if (ticket.getXaOID() != null) {
                    Address address = addressService.findById(ticket.getXaOID());
                    if (address != null)
                        ticket.setAddress(address);
                }
                
                if (ticket.getSubmitterID() != null) {
                    User user = userMapper.getUserById(ticket.getSubmitterID());
                    String company = userMapper.getCompanyName(user.getCompanyId());
                    ticket.setSubmitterOrg(company);
                    ticket.setSubmitterEmail(user.getEmail());
                    ticket.setSubmitterName(user.getName());
                }
                
                List<TrackingNum> trackingNumber = ticketMapper.getTrackingNumber(id);
                ticket.setTrackingNumbers(trackingNumber);
                
                Map<String, Object> ticketingViewsMap = objectMapper.convertValue(ticket, Map.class);
                
                return new QueryResultDTO(ticketingViewsMap, 0, "");
            } else {
                return new QueryResultDTO(null, -1, "Ticket Not found");
            }
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultDTO insertResponse(TicketResponse ticketResponse) {
        CustomUserDetails user = AuthUtil.getUser();
        
        try {
            if (user != null) {
                ticketResponse.setResponseBy(user.getUserId().toString());
            }
            ticketMapper.insertResponse(ticketResponse);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("response", ticketResponse);
            return new QueryResultDTO(resultMap, 0, "");
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }
    
    
    @Override
    public QueryResultArrayDTO getResponse(String id) {
        if (!userHasAccess(id)) {
            return new QueryResultArrayDTO(null, 0, -1, "Don't have access to the ticket");
        }
        try {
            ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
            
            List<TicketResponse> responsesList = ticketMapper.getResponse(id);
            
            if (!responsesList.isEmpty()) {
                for (TicketResponse ticketingResponse : responsesList) {
                    
                    Map<String, Object> responsesMap = objectMapper.convertValue(ticketingResponse, Map.class);
                    resultArray.add(responsesMap);
                }
            }
            return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO editTicket(String id, TicketEditDTO ticketEditDTO) {
        if (!userHasAccess(id)) {
            return new QueryResultArrayDTO(null, 0, -1, "Don't have access to the ticket");
        }
        try {
            //update tracking number part
            if (ticketEditDTO.isFromMaster()) {
                //    void updateMasterOrder(Integer typeOfRepair, String originalRMA, String moOID, String xaOID);
                Integer xaOID = null;
                if (ticketEditDTO.getAddress() != null) {
                    xaOID = ticketEditDTO.getAddress().getXaOid();
                }
                ticketMapper.updateMasterOrder(ticketEditDTO.getTypeOfRepair(), ticketEditDTO.getOriginalRMA(), id, xaOID, ticketEditDTO.getTestKeyType());
            } else {
                Integer xaOID = null;
                if (ticketEditDTO.getAddress() != null) {
                    xaOID = ticketEditDTO.getAddress().getXaOid();
                }
                ticketMapper.updatePrepMasterOrder(ticketEditDTO.getTypeOfRepair(), ticketEditDTO.getOriginalRMA(), id, xaOID, ticketEditDTO.getTestKeyType());
            }
            if (ticketEditDTO.getUpdateTracking().size() > 0) {
                ticketMapper.updateXref_Inbound_Tracking(ticketEditDTO.getUpdateTracking());
            }
            if (ticketEditDTO.getDeleteTracking().size() > 0) {
                ticketMapper.deleteXref_Inbound_Tracking(ticketEditDTO.getDeleteTracking());
            }
            if (ticketEditDTO.getAddTracking().size() > 0) {
                if (ticketEditDTO.getAddTracking().size() == 1) {
                    ticketMapper.insertSingleXref_Inbound_Tracking(ticketEditDTO.getAddTracking().get(0));
                } else {
                    ticketMapper.batchInsertXref_Inbound_Tracking(ticketEditDTO.getAddTracking());
                }
            }
            
            return new QueryResultArrayDTO(null, 0, 0, "");
        } catch (Exception e) {
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
    public QueryResultArrayDTO batchSerialNumberQuery(EntityManager entityManager, MultipartFile file, String fileName) {
        Workbook workbook = null;
        Sheet sheet = null;
        Row row = null;
        fileName = fileName.replaceAll("\\s", "_");
        fileName = fileName.replaceAll(".xlsx", "");
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();//use to store final result and return to front end
        int totalSerialNumber = 0;
        List<String> serialNumbersInFile = new ArrayList<>();
        HashMap<String, String[]> deviceInfoMap = new HashMap<>();
        
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
                    //
                    serialNumbersInFile.add(currSerialNumber);
                    String[] temp = new String[3];
                    temp[0] = QueryUtils.getCellValue(sheet.getRow(j).getCell(1)); //customer reported issue
                    temp[1] = QueryUtils.getCellValue(sheet.getRow(j).getCell(2)); // customer RMA
                    temp[2] = QueryUtils.getCellValue(sheet.getRow(j).getCell(3)); //terminalID
                    deviceInfoMap.put(currSerialNumber, temp);
                }
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            if (sheet != null && row != null)
                msg = msg + " sheet: " + sheet.getSheetName() + ", row: " + row.getRowNum();
            return new QueryResultArrayDTO(null, 0, -1, msg);
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (IOException e) {
                return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
            }
        }
        
        resultArray = getResult(serialNumbersInFile, deviceInfoMap);
        return new QueryResultArrayDTO(resultArray, totalSerialNumber, 0, "");
    }
    
    private ArrayList<Map<String, Object>> getResult(List<String> serialNumberList, HashMap<String, String[]> deviceInfoMap) {
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        List<String> usBasedDevices = ticketMapper.findUSBasedDevices(serialNumberList);
        List<String> notUSBasedDevices = new ArrayList<>();
        for (String s : serialNumberList) {
            if (!usBasedDevices.contains(s)) {
                notUSBasedDevices.add(s);
            }
        }
        
        for (String nus : notUSBasedDevices) {
            Map<String, Object> batchDeviceInfo = new HashMap<>();
            batchDeviceInfo.put("serialNumber", nus);
            batchDeviceInfo.put("errorMsg", "This is not a U.S.Device or you input the wrong Serial Number.");
            batchDeviceInfo.put("resultCode", -1);
            resultArray.add(batchDeviceInfo);
        }
        
        //if there is no us-based devices, return it the result array directly
        if (usBasedDevices.isEmpty()) {
            return resultArray;
        }
        
        CustomUserDetails user = AuthUtil.getUser();
        String companyId = String.valueOf(user.getCompanyId());
        List<Device> getDevice = ticketMapper.getDeviceInfos(usBasedDevices, companyId);
        
        for (Device d : getDevice) {
            Map<String, Object> batchDeviceInfo = new HashMap<>();
//            String errorMsg = "";
            String curSN = d.getSerialNumber();
            batchDeviceInfo.put("serialNumber", curSN);
            batchDeviceInfo.put("xmOID", d.getXmOID());
            batchDeviceInfo.put("pxmOID", d.getPxmOID());
            batchDeviceInfo.put("cosmetic", d.getCosmetic());
            batchDeviceInfo.put("msnOID", d.getMsnOID());
            batchDeviceInfo.put("model", d.getModel());
            batchDeviceInfo.put("version", d.getVersion());
            batchDeviceInfo.put("customerReportedIssueExt", deviceInfoMap.get(curSN)[0]);
            batchDeviceInfo.put("customerRMA", deviceInfoMap.get(curSN)[1]);
            batchDeviceInfo.put("customerTerminalID", deviceInfoMap.get(curSN)[2]);
            batchDeviceInfo.put("warrantyExpDate", d.getWarrantyExpDate());
            batchDeviceInfo.put("warrantyStatus", d.getWarrantyStatus());
            batchDeviceInfo.put("cosmeticPrice", d.getCosmeticPrice());
            batchDeviceInfo.put("diagnosticPrice", d.getDiagnosticPrice());
            batchDeviceInfo.put("minorPrice", d.getMinorPrice());
            batchDeviceInfo.put("existInAnotherTicket", d.getExistInAnotherTicket());
            batchDeviceInfo.put("moOID", d.getMoOID());
            batchDeviceInfo.put("keyInjection", d.getKeyInjection());
            resultArray.add(batchDeviceInfo);
        }
        return resultArray;
    }
    
    @Override
    public QueryResultArrayDTO serialNumberQuery(String serialNumber) {
        ArrayList<Map<String, Object>> resultArray = new ArrayList<>(); //use to store final result and return to front end
        List<String> serialNumberList = new ArrayList<>();
        HashMap<String, String[]> deviceInfoMap = new HashMap<>();
        serialNumberList.add(serialNumber);
        String[] temp = new String[3];
        temp[0] = ""; // customer reported issue, there is no need to pass these parameters when query device information, only when submitting ticket. These information will be stored in database.
        temp[1] = ""; // customer RMA
        temp[2] = ""; // terminalID
        deviceInfoMap.put(serialNumber, temp);
        resultArray = getResult(serialNumberList, deviceInfoMap);
        return new QueryResultArrayDTO(resultArray, 1, 0, "");
    }
    
    
    @Override
    public int insertTicketToPMO(TicketInsertionObject tio) { // PMO is prep_master_order
        CustomUserDetails user = AuthUtil.getUser();
        Integer companyId = user.getCompanyId();
        tio.setSubmitterID(user.getUserId());
        tio.setMcOID(companyId);
        tio.setOrderStatus("12");
        tio.setOrderDateToCurrentDate();
        ticketMapper.insertPrep_Master_Order(tio);
        return tio.getMoOID();
    }

    @Override
    public CompletableFuture<QueryResultDTO> submitTicketFuture(TicketInsertion ticketInsertion) {
        try {
            final CustomUserDetails user = AuthUtil.getUser();
            if (user == null) {
                return CompletableFuture.completedFuture(
                        new QueryResultDTO(null, -1, "Unable to load account details")
                );
            }
            String clientEmail = user.getEmailAddress();
            Integer clientId = user.getUserId();
            Integer companyId = user.getCompanyId();

            List<SNsInsertionObject> insertedSerials = ticketInsertion.getSerials();
            List<String> trackingNumbers = ticketInsertion.getTrackingNumbers();
            Integer orderType = ticketInsertion.getOrderType();
            String originalRMA = ticketInsertion.getOriginalRMA();
            Integer xaOId = ticketInsertion.getXaOID();

            TicketInsertionObject tio = new TicketInsertionObject();
            tio.setOrderType(orderType);
            tio.setRmaNumber(originalRMA);
            tio.setSubmitterID(clientId);
            tio.setXaOID(xaOId);
            tio.setTestKeyType(ticketInsertion.getTestKeyType());
            tio.setEncrypt(ticketInsertion.getEncrypt());
            int moOID = insertTicketToPMO(tio);

            for (SNsInsertionObject snsObject : insertedSerials) {
                snsObject.setMoOID(moOID);
            }

            ticketMapper.insertPrep_Xref_Materials(insertedSerials);
            List<Integer> pxmOidList = new ArrayList<>();
            insertedSerials.forEach(s -> pxmOidList.add(s.getXmOID()));
            invoiceService.insertInvoiceList(pxmOidList, companyId);
            Double invoice = invoiceService.getTotalInvoice(moOID);
            if (!trackingNumbers.isEmpty()) {
                ticketMapper.insertXref_Inbound_Tracking(trackingNumbers, moOID);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("mo_OID", moOID);
            Company company = userMapper.getCompanyInfo(companyId);
            Integer clientGroup = company.getClientGroupId();
            String content = constructEmail(moOID, invoice, clientGroup);
            String subject = String.format("RMA #%d Confirmation", moOID);

            return emailService.sendEmail(clientEmail, subject, content)
                    .thenApply(sesResponse -> {
                        String emailResult = sesResponse.isSuccess() ?
                                "Success, message ID: " + sesResponse.getResponse().messageId() :
                                "Email sending failed";
                        result.put("emailDeliveryResult", emailResult);
                        return new QueryResultDTO(result, 0, "");
                    })
                    .exceptionally(e -> {
                        log.error("Error sending email: " + e.getMessage(), e);
                        result.put("emailDeliveryResult", "Failed to send email");
                        return new QueryResultDTO(result, -1, e.getMessage());
                    });
        } catch (Exception e) {
            log.error("Error processing ticket submission: " + e.getMessage(), e);
            return CompletableFuture.completedFuture(new QueryResultDTO(null, -1, e.getMessage()));
        }
    }

    @Override
    public QueryResultDTO submitTicket(TicketInsertion ticketInsertion) {
        Integer submitterId = AuthUtil.getUser().getUserId();
        String submitterEmail = AuthUtil.getUser().getEmailAddress();
        Integer companyId = AuthUtil.getUser().getCompanyId();
        
        List<SNsInsertionObject> sNsInsertionObjectList = ticketInsertion.getSerials();
        List<String> trackingNumbers = ticketInsertion.getTrackingNumbers();
        
        Integer orderType = ticketInsertion.getOrderType();
        String originalRMA = ticketInsertion.getOriginalRMA();
        Integer xaOId = ticketInsertion.getXaOID();
        
        TicketInsertionObject tio = new TicketInsertionObject();
        
        tio.setOrderType(orderType);
        tio.setRmaNumber(originalRMA);
        tio.setSubmitterID(submitterId);
        tio.setXaOID(xaOId);
        tio.setTestKeyType(ticketInsertion.getTestKeyType());
        tio.setEncrypt(ticketInsertion.getEncrypt());
        int mo_OID = insertTicketToPMO(tio);
        
        for (SNsInsertionObject snsObject : sNsInsertionObjectList) {
            snsObject.setMoOID(mo_OID);
        }
        try {
            ticketMapper.insertPrep_Xref_Materials(sNsInsertionObjectList);
            List<Integer> pxmOidList = new ArrayList<>();
            sNsInsertionObjectList.stream().forEach(s -> pxmOidList.add(s.getXmOID()));
            invoiceService.insertInvoiceList(pxmOidList, companyId);
            Double invoice = invoiceService.getTotalInvoice(mo_OID);
            if (!trackingNumbers.isEmpty()) {
                ticketMapper.insertXref_Inbound_Tracking(trackingNumbers, mo_OID);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("mo_OID", mo_OID);
            Company company = userMapper.getCompanyInfo(companyId);
            Integer clientGroup = company.getClientGroupId();
            String content = constructEmail(mo_OID, invoice, clientGroup);
            String subject = String.format("RMA #%d Confirmation", mo_OID);
            submitterEmail = "success@simulator.amazonses.com";
//            Mono<String> delivery = emailService.sendEmail(submitterEmail, subject, content).map(response ->
//                    response.isSuccess() ? "Success, message ID: " + response.getResponse().messageId()
//                            : response.getException() != null ? response.getException().getMessage()
//                            : "Service Disabled");
//            delivery.subscribe(
//                    value -> {
//                        result.put("emailDeliveryResult", value);
//                    },
//                    error -> {
//                        log.error(error.getMessage());
//                    }
//            );
            return new QueryResultDTO(result, 0, "");
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }
    
    private String constructEmail(Integer moOID, Double invoice, Integer clientGroup) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("moOID", moOID);
        map.put("invoice", invoice);
        
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = null;
        if (clientGroup.equals(458) && invoice > 0d) {
            //small client
            mustache = mf.compile("html/email/smallMktRmaEmail.mustache");
            
        } else {
            mustache = mf.compile("html/email/midLargeRmaEmail.mustache");
        }
        StringWriter writer = new StringWriter();
        mustache.execute(writer, map).flush();
        String emailBody = writer.toString();
        return emailBody;
    }
    
    public QueryResultDTO ackTicket(Long moOID) {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        
        if (user.isClientUser()) {
            return new QueryResultDTO(null, -1, "Don't have access to the ticket");
        }
        try {
            ticketMapper.ackMasterTicket(moOID);
            ticketMapper.ackPrepMasterTicket(moOID);
            return new QueryResultDTO(null, 0, null);
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }
    
    public QueryResultDTO unAckTicket(Long moOID) {
        if (!userHasAccess(String.valueOf(moOID))) {
            return new QueryResultDTO(null, -1, "Don't have access to the ticket");
        }
        try {
            ticketMapper.unAckMasterTicket(moOID);
            ticketMapper.unAckPrepMasterTicket(moOID);
            return new QueryResultDTO(null, 0, null);
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultDTO getTicketAckStatus(Long moOID) {
        if (!userHasAccess(String.valueOf(moOID))) {
            return new QueryResultDTO(null, -1, "Don't have access to the ticket");
        }
        try {
            List<Integer> list = ticketMapper.getTicketAckStatus(moOID);
            if (list.size() != 1) {
                return new QueryResultDTO(null, -1, "Ticket not found");
            }
            Integer ack = list.get(0);
            Map<String, Object> map = new HashMap<>();
            map.put("acknowledged", ack);
            return new QueryResultDTO(map, 0, null);
        } catch (Exception e) {
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryCustomerOrg() {
        try {
            CustomUserDetails user = AuthUtil.getUser();
            assert user != null;
            //user is client and has same mcoid with ticket or user is pax employee
            if (user.isClientUser()) {
                return new QueryResultArrayDTO(null, 0, -1, "Don't have access to the resource.");
            }
            List<Customer> customers = ticketMapper.getAllCustomerOrg();
            
            ArrayList<Map<String, Object>> result = new ArrayList<>();
            for (Customer customer : customers) {
                Map<String, Object> mm = new LinkedHashMap<>();
                mm.put(DropDownConstant.DROPDOWN_VALUE, customer.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, customer.getCustomerName());
                result.add(mm);
            }
            return new QueryResultArrayDTO(result, result.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryKeyType() {
        try {
            List<String> keys = ticketMapper.getAllKeyType();
            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
            
            for (int i = 0; i < keys.size(); ++i) {
                Map<String, Object> mm = new LinkedHashMap<>();
                mm.put(DropDownConstant.DROPDOWN_VALUE, i);
                mm.put(DropDownConstant.DROPDOWN_LABEL, keys.get(i));
                jsonArray.add(mm);
            }
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryKeyKcv(String keyType) {
        try {
            List<Key> keys = ticketMapper.getAllKey(keyType, null);
            
            ArrayList<Map<String, Object>> result = new ArrayList<>();
            for (Key key : keys) {
                Map<String, Object> mm = new LinkedHashMap<>();
                mm.put(DropDownConstant.DROPDOWN_VALUE, key.getKeyIndex());
                mm.put(DropDownConstant.DROPDOWN_LABEL, key.getKcv());
                result.add(mm);
            }
            return new QueryResultArrayDTO(result, result.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryKeyKsi(String keyType, String kcv) {
        try {
            List<Key> keys = ticketMapper.getAllKey(keyType, kcv);
            
            ArrayList<Map<String, Object>> result = new ArrayList<>();
            for (Key key : keys) {
                Map<String, Object> mm = new LinkedHashMap<>();
                mm.put(DropDownConstant.DROPDOWN_VALUE, key.getKeyIndex());
                mm.put(DropDownConstant.DROPDOWN_LABEL, key.getKsi());
                result.add(mm);
            }
            return new QueryResultArrayDTO(result, result.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }
    
    @Override
    public QueryResultArrayDTO queryKey() {
        try {
            List<Key> keys = ticketMapper.getAllKeys();
            
            ArrayList<Map<String, Object>> result = new ArrayList<>();
            for (Key key : keys) {
                if (key.getKeyType() == null || key.getKeyType().length() == 0) {
                    key.setKeyType("N/A");
                }
                
                if (key.getKcv() == null || key.getKcv().length() == 0) {
                    key.setKcv("N/A");
                }
                
                if (key.getKsi() == null || key.getKsi().length() == 0) {
                    key.setKsi("N/A");
                }
                
                Map<String, Object> mm = new LinkedHashMap<>();
                Map<String, Object> keyMap = objectMapper.convertValue(key, Map.class);
                
                mm.put(DropDownConstant.DROPDOWN_VALUE, key.getKeyIndex());
                mm.put(DropDownConstant.DROPDOWN_LABEL, keyMap);
                result.add(mm);
            }
            return new QueryResultArrayDTO(result, result.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    /**
     * 上传工单附件并记录到 MASTER_RMA_FILE_STORAGE。
     *
     * 业务规则：
     * 1. 文件类型与大小限制在后端再次严格校验，防止绕过前端。
     * 2. 支持多文件上传。
     * 3. 可携带备注，备注写入 XREF_RESPONSE 作为工单消息。
     * 4. 每个上传动作写入审计日志。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryResultArrayDTO uploadTicketAttachments(Integer ticketId, String remark, List<MultipartFile> files) {
        if (ticketId == null) {
            return new QueryResultArrayDTO(null, 0, -1, "Ticket ID is required.");
        }

        if (!userHasAccess(String.valueOf(ticketId))) {
            return new QueryResultArrayDTO(null, 0, -1, "Don't have access to the ticket");
        }

        List<MultipartFile> uploadFiles = files == null ? Collections.emptyList() : files;
        boolean hasRemark = remark != null && remark.trim().length() > 0;
        if (uploadFiles.isEmpty() && !hasRemark) {
            return new QueryResultArrayDTO(null, 0, -1, "At least one attachment or remark is required.");
        }

        ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
        try {
            for (MultipartFile file : uploadFiles) {
                String validationError = validateAttachmentFile(file);
                if (validationError != null) {
                    return new QueryResultArrayDTO(null, 0, -1, validationError);
                }

                RmaAttachmentStorageService.StoredAttachment storedAttachment =
                        rmaAttachmentStorageService.upload(ticketId, file);

                RmaFileStorage fileStorage = new RmaFileStorage();
                fileStorage.setMoOID(ticketId);
                fileStorage.setFileName(storedAttachment.getOriginalFileName());
                fileStorage.setPath(storedAttachment.getObjectKey());
                fileStorage.setSize(String.valueOf(storedAttachment.getFileSize()));
                fileStorage.setType(storedAttachment.getContentType());

                try {
                    rmaFileStorageMapper.insertRmaFileStorage(fileStorage);
                } catch (Exception dbEx) {
                    // 数据库存储失败时回滚对象存储，避免“孤儿文件”。
                    try {
                        rmaAttachmentStorageService.delete(storedAttachment.getObjectKey());
                    } catch (Exception deleteEx) {
                        log.error("附件回滚删除失败，ticketId={}, key={}", ticketId, storedAttachment.getObjectKey(), deleteEx);
                    }
                    throw dbEx;
                }

                String downloadUrl = rmaAttachmentStorageService.generateDownloadUrl(storedAttachment.getObjectKey());
                Map<String, Object> fileMap = new LinkedHashMap<>();
                fileMap.put("fileId", fileStorage.getMrfOID());
                fileMap.put("ticketId", ticketId);
                fileMap.put("fileName", fileStorage.getFileName());
                fileMap.put("path", fileStorage.getPath());
                fileMap.put("size", fileStorage.getSize());
                fileMap.put("type", fileStorage.getType());
                fileMap.put("downloadUrl", downloadUrl);
                resultArray.add(fileMap);

                writeAttachmentAudit("UPLOAD", ticketId, fileStorage.getMrfOID(), fileStorage.getFileName(), fileStorage.getPath());
            }

            if (hasRemark) {
                CustomUserDetails user = AuthUtil.getUser();
                TicketResponse ticketResponse = new TicketResponse();
                ticketResponse.setMoOID(ticketId);
                ticketResponse.setContent(remark.trim());
                if (user != null) {
                    ticketResponse.setResponseBy(String.valueOf(user.getUserId()));
                }
                ticketMapper.insertResponse(ticketResponse);
                writeAttachmentAudit("REMARK", ticketId, null, "remark", "XREF_RESPONSE");
            }

            return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        } catch (Exception e) {
            log.error("上传工单附件失败，ticketId={}", ticketId, e);
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    /**
     * 查询工单附件列表，并附带下载 URL（S3 预签名或本地 token URL）。
     */
    @Override
    public QueryResultArrayDTO listTicketAttachments(Integer ticketId) {
        if (ticketId == null) {
            return new QueryResultArrayDTO(null, 0, -1, "Ticket ID is required.");
        }

        if (!userHasAccess(String.valueOf(ticketId))) {
            return new QueryResultArrayDTO(null, 0, -1, "Don't have access to the ticket");
        }

        try {
            List<RmaFileStorage> fileStorageList = rmaFileStorageMapper.selectByTicketId(ticketId);
            ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
            for (RmaFileStorage fileStorage : fileStorageList) {
                Map<String, Object> fileMap = new LinkedHashMap<>();
                fileMap.put("fileId", fileStorage.getMrfOID());
                fileMap.put("ticketId", fileStorage.getMoOID());
                fileMap.put("fileName", fileStorage.getFileName());
                fileMap.put("path", fileStorage.getPath());
                fileMap.put("size", fileStorage.getSize());
                fileMap.put("type", fileStorage.getType());
                fileMap.put("downloadUrl", rmaAttachmentStorageService.generateDownloadUrl(fileStorage.getPath()));
                resultArray.add(fileMap);
            }
            return new QueryResultArrayDTO(resultArray, resultArray.size(), 0, "");
        } catch (Exception e) {
            log.error("查询工单附件列表失败，ticketId={}", ticketId, e);
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    /**
     * 生成单个附件下载 URL，并记录“下载行为审计日志”。
     */
    @Override
    public QueryResultDTO generateAttachmentDownloadUrl(Integer ticketId, Integer fileId) {
        if (ticketId == null || fileId == null) {
            return new QueryResultDTO(null, -1, "Ticket ID and file ID are required.");
        }

        if (!userHasAccess(String.valueOf(ticketId))) {
            return new QueryResultDTO(null, -1, "Don't have access to the ticket");
        }

        try {
            RmaFileStorage fileStorage = rmaFileStorageMapper.selectByTicketIdAndFileId(ticketId, fileId);
            if (fileStorage == null) {
                return new QueryResultDTO(null, -1, "Attachment not found.");
            }

            String downloadUrl = rmaAttachmentStorageService.generateDownloadUrl(fileStorage.getPath());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("fileId", fileStorage.getMrfOID());
            result.put("ticketId", fileStorage.getMoOID());
            result.put("fileName", fileStorage.getFileName());
            result.put("downloadUrl", downloadUrl);

            writeAttachmentAudit("DOWNLOAD", ticketId, fileStorage.getMrfOID(), fileStorage.getFileName(), fileStorage.getPath());
            return new QueryResultDTO(result, 0, "");
        } catch (Exception e) {
            log.error("生成附件下载地址失败，ticketId={}, fileId={}", ticketId, fileId, e);
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }

    /**
     * 删除工单附件（对象存储 + 数据库记录）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryResultDTO deleteTicketAttachment(Integer ticketId, Integer fileId) {
        if (ticketId == null || fileId == null) {
            return new QueryResultDTO(null, -1, "Ticket ID and file ID are required.");
        }

        if (!userHasAccess(String.valueOf(ticketId))) {
            return new QueryResultDTO(null, -1, "Don't have access to the ticket");
        }

        try {
            RmaFileStorage fileStorage = rmaFileStorageMapper.selectByTicketIdAndFileId(ticketId, fileId);
            if (fileStorage == null) {
                return new QueryResultDTO(null, -1, "Attachment not found.");
            }

            rmaAttachmentStorageService.delete(fileStorage.getPath());
            rmaFileStorageMapper.deleteByFileId(fileId);

            writeAttachmentAudit("DELETE", ticketId, fileStorage.getMrfOID(), fileStorage.getFileName(), fileStorage.getPath());
            return new QueryResultDTO(null, 0, "");
        } catch (Exception e) {
            log.error("删除附件失败，ticketId={}, fileId={}", ticketId, fileId, e);
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }

    /**
     * 本地回退模式下的 token 下载实现。
     * 说明：S3 模式不会进入该分支，前端会直接拿到 S3 预签名 URL。
     */
    @Override
    public void downloadLocalAttachment(String token, HttpServletResponse response) throws IOException {
        RmaAttachmentStorageService.LocalDownloadResource downloadResource =
                rmaAttachmentStorageService.resolveLocalDownload(token);
        if (downloadResource == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Attachment token is invalid or expired.");
            return;
        }

        Path localPath = downloadResource.getLocalPath();
        String contentType = Files.probeContentType(localPath);
        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "application/octet-stream";
        }

        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + localPath.getFileName().toString() + "\"");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        response.setDateHeader("Expires", Instant.now().toEpochMilli());

        try (InputStream inputStream = Files.newInputStream(localPath)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                response.getOutputStream().write(buffer, 0, length);
            }
            response.flushBuffer();
        }
    }

    /**
     * Contact RMA：发送支持邮件（支持可选截图附件）。
     * @param ticketId 工单号（可选）
     * @param subject 主题（必填）
     * @param message 内容（必填）
     * @param screenshot 图片附件（可选）
     */
    @Override
    public QueryResultDTO contactRma(String ticketId, String subject, String message, MultipartFile screenshot) {
        CustomUserDetails user = AuthUtil.getUser();
        if (user == null) {
            return new QueryResultDTO(null, -1, "Unable to load account details.");
        }

        String normalizedSubject = subject == null ? "" : subject.trim();
        String normalizedMessage = message == null ? "" : message.trim();
        if (normalizedSubject.isEmpty()) {
            return new QueryResultDTO(null, -1, "Subject is required.");
        }
        if (normalizedMessage.isEmpty()) {
            return new QueryResultDTO(null, -1, "Message is required.");
        }

        String normalizedTicketId = ticketId == null ? "" : ticketId.trim();
        if (!normalizedTicketId.isEmpty() && normalizedTicketId.matches("\\d+")) {
            if (!userHasAccess(normalizedTicketId)) {
                return new QueryResultDTO(null, -1, "Don't have access to the ticket");
            }
        }

        String screenshotValidationError = validateContactRmaScreenshot(screenshot);
        if (screenshotValidationError != null) {
            return new QueryResultDTO(null, -1, screenshotValidationError);
        }

        String customerName = user.getUsername();
        String customerEmail = user.getEmailAddress();
        String customerOrganization = "N/A";
        try {
            String companyName = userMapper.getCompanyName(user.getCompanyId());
            if (companyName != null && companyName.trim().length() > 0) {
                customerOrganization = companyName.trim();
            }
        } catch (Exception e) {
            log.warn("Contact RMA query company name failed, companyId={}", user.getCompanyId(), e);
        }

        String submitTimestamp = Instant.now().toString();
        String emailSubject = "[Contact RMA] " + normalizedSubject
                + (normalizedTicketId.isEmpty() ? "" : " | Ticket " + normalizedTicketId);
        String emailBody = buildContactRmaEmailBody(
                normalizedMessage,
                normalizedTicketId,
                customerName,
                customerOrganization,
                customerEmail,
                submitTimestamp
        );

        try {
            String attachmentFileName = null;
            String attachmentContentType = null;
            byte[] attachmentBytes = null;

            if (screenshot != null && !screenshot.isEmpty()) {
                attachmentFileName = screenshot.getOriginalFilename();
                attachmentContentType = screenshot.getContentType();
                attachmentBytes = screenshot.getBytes();
            }

            boolean success = emailService.sendEmailWithAttachment(
                    CONTACT_RMA_EMAIL,
                    emailSubject,
                    emailBody,
                    attachmentFileName,
                    attachmentContentType,
                    attachmentBytes
            ).join().isSuccess();

            if (!success) {
                writeContactRmaSubmissionAudit("FAIL", normalizedTicketId, customerName, submitTimestamp, normalizedSubject);
                return new QueryResultDTO(null, -1, "Failed to send email to RMAsupport@pax.us");
            }

            writeContactRmaSubmissionAudit("PASS", normalizedTicketId, customerName, submitTimestamp, normalizedSubject);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("to", CONTACT_RMA_EMAIL);
            result.put("ticketId", normalizedTicketId.isEmpty() ? null : normalizedTicketId);
            result.put("timestamp", submitTimestamp);
            result.put("user", customerName);
            return new QueryResultDTO(result, 0, "");
        } catch (Exception e) {
            log.error("Contact RMA send email failed, ticketId={}, user={}", normalizedTicketId, customerName, e);
            writeContactRmaSubmissionAudit("FAIL", normalizedTicketId, customerName, submitTimestamp, normalizedSubject);
            return new QueryResultDTO(null, -1, e.getMessage());
        }
    }

    /**
     * 校验附件类型与大小。
     *
     * @param file 上传文件
     * @return 校验失败返回错误信息；成功返回 null
     */
    /**
     * 校验 Contact RMA 的截图附件（仅允许图片）。
     */
    private String validateContactRmaScreenshot(MultipartFile screenshot) {
        if (screenshot == null || screenshot.isEmpty()) {
            return null;
        }

        String extension = getFileExtension(screenshot.getOriginalFilename());
        if (extension == null || !CONTACT_RMA_IMAGE_EXTENSIONS.contains(extension)) {
            return "Unsupported screenshot format. Only JPG/JPEG/PNG/GIF are allowed.";
        }

        if (screenshot.getSize() > MAX_DOC_IMAGE_SIZE) {
            return "Screenshot exceeds 10 MB limit.";
        }
        return null;
    }

    /**
     * 构建 Contact RMA 邮件正文（HTML）。
     */
    private String buildContactRmaEmailBody(String message,
                                            String ticketId,
                                            String customerName,
                                            String customerOrganization,
                                            String customerEmail,
                                            String timestamp) {
        String safeMessage = escapeHtml(message).replace("\n", "<br/>");
        String safeTicketId = (ticketId == null || ticketId.isEmpty()) ? "N/A" : escapeHtml(ticketId);
        String safeCustomerName = escapeHtml(customerName);
        String safeCustomerOrganization = escapeHtml(customerOrganization);
        String safeCustomerEmail = escapeHtml(customerEmail);
        String safeTimestamp = escapeHtml(timestamp);

        return "<html><body>"
                + "<h3>Contact RMA Request</h3>"
                + "<p><strong>Ticket ID:</strong> " + safeTicketId + "</p>"
                + "<p><strong>Customer Name:</strong> " + safeCustomerName + "</p>"
                + "<p><strong>Organization:</strong> " + safeCustomerOrganization + "</p>"
                + "<p><strong>Email:</strong> " + safeCustomerEmail + "</p>"
                + "<p><strong>Submitted At:</strong> " + safeTimestamp + "</p>"
                + "<hr/>"
                + "<p><strong>Message:</strong></p>"
                + "<p>" + safeMessage + "</p>"
                + "</body></html>";
    }

    /**
     * Contact RMA 提交审计日志。
     * 日志字段包含：timestamp、user、ticketId、subject、status。
     */
    private void writeContactRmaSubmissionAudit(String status,
                                                String ticketId,
                                                String userName,
                                                String timestamp,
                                                String subject) {
        Integer progOid = null;
        try {
            progOid = LoggingUtil.addLogProgram(logProgramMapper, "CONTACT_RMA_SUBMISSION");
            String info = String.format(
                    "status=%s, timestamp=%s, user=%s, ticketId=%s, subject=%s",
                    status,
                    timestamp,
                    userName,
                    (ticketId == null || ticketId.isEmpty()) ? "N/A" : ticketId,
                    subject
            );
            LoggingUtil.addLogProgramInfo(logProgramMapper, progOid, info);
            LoggingUtil.updateLogProgram(logProgramMapper, progOid, status);
            log.info("[CONTACT_RMA_AUDIT] {}", info);
        } catch (Exception e) {
            log.error("Contact RMA write audit log failed.", e);
            if (progOid != null) {
                try {
                    LoggingUtil.updateLogProgram(logProgramMapper, progOid, LoggingUtil.FAIL);
                } catch (Exception ignored) {
                    // 审计补偿异常不影响主流程。
                }
            }
        }
    }

    /**
     * HTML 字符转义，防止用户输入直接拼接到 HTML。
     */
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String validateAttachmentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Attachment cannot be empty.";
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (extension == null || extension.length() == 0) {
            return "Unsupported file type: " + originalFilename;
        }

        boolean isDocument = DOCUMENT_EXTENSIONS.contains(extension);
        boolean isImage = IMAGE_EXTENSIONS.contains(extension);
        boolean isVideo = VIDEO_EXTENSIONS.contains(extension);
        if (!isDocument && !isImage && !isVideo) {
            return "Unsupported file type: " + originalFilename;
        }

        long size = file.getSize();
        if ((isDocument || isImage) && size > MAX_DOC_IMAGE_SIZE) {
            return "File exceeds 10 MB limit: " + originalFilename;
        }
        if (isVideo && size > MAX_VIDEO_SIZE) {
            return "File exceeds 500 MB limit: " + originalFilename;
        }
        return null;
    }

    /**
     * 提取文件扩展名（小写）。
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int idx = fileName.lastIndexOf('.');
        if (idx < 0 || idx == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(idx + 1).toLowerCase();
    }

    /**
     * 写入附件审计日志。
     *
     * 说明：
     * 1. 通过 LOG_PROGRAM / LOG_PROGRAM_INFO 记录“上传、下载、删除、备注”动作。
     * 2. 同时输出应用日志，便于排障与追踪。
     */
    private void writeAttachmentAudit(String action, Integer ticketId, Integer fileId, String fileName, String path) {
        String programName = "RMA_ATTACHMENT_" + action;
        Integer progOid = null;
        try {
            progOid = LoggingUtil.addLogProgram(logProgramMapper, programName);
            CustomUserDetails user = AuthUtil.getUser();
            String userName = user == null ? "unknown" : user.getUsername();
            String info = String.format("action=%s, ticketId=%s, fileId=%s, fileName=%s, path=%s, user=%s, at=%s",
                    action, ticketId, fileId, fileName, path, userName, Instant.now().toString());
            LoggingUtil.addLogProgramInfo(logProgramMapper, progOid, info);
            LoggingUtil.updateLogProgram(logProgramMapper, progOid, LoggingUtil.PASS);
            log.info("[RMA_ATTACHMENT_AUDIT] {}", info);
        } catch (Exception e) {
            log.error("写入附件审计日志失败，action={}, ticketId={}, fileId={}", action, ticketId, fileId, e);
            if (progOid != null) {
                try {
                    LoggingUtil.updateLogProgram(logProgramMapper, progOid, LoggingUtil.FAIL);
                } catch (Exception ignored) {
                    // 忽略审计日志补偿异常，避免影响主流程结果返回。
                }
            }
        }
    }
    
    private Boolean userHasAccess(String id) {
        CustomUserDetails user = AuthUtil.getUser();
        assert user != null;
        
        TicketInfo ticket = ticketMapper.existingMasterOrder(id);
        if (ticket == null) {
            ticket = ticketMapper.existingPREPMasterOrder(id);
        }

        // 工单不存在时直接判定无权限，避免空指针。
        if (ticket == null) {
            return false;
        }
        
        return user.canViewOrEditOtherCustomersRecords(ticket.getMcOID());
    }
}
