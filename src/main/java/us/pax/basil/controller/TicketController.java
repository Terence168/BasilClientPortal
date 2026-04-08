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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.ticket.Key;
import us.pax.basil.entity.ticket.TicketEditDTO;
import us.pax.basil.entity.ticket.TicketInsertion;
import us.pax.basil.entity.ticket.TicketResponse;
import us.pax.basil.service.TicketService;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Api(tags = "Basil API Interface")
@RestController
@RequestMapping("/ticketing")
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    private EntityManager entityManager;
    
    //search serial number and return device information and repair price.
    @PreAuthorize("hasAnyAuthority('ticketing.add', 'ticketing.update')")
    @GetMapping("/serialNumberUpdate")//BCP-25
    public QueryResultArrayDTO serialNumberUpdate(@RequestParam(value = "serialNumber", required = true) String serialNumber) {
        return ticketService.serialNumberQuery(serialNumber);
    }
    
    //upload Excel file, process serial number by batch processing.
    @PreAuthorize("hasAnyAuthority('ticketing.add', 'ticketing.update')")
    @PostMapping("/batchSerialNumberQuery")
    public QueryResultArrayDTO batchSerialNumberUpload(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("fileName") String fileName
    ) {
        return ticketService.batchSerialNumberQuery(entityManager, file, fileName);
    }
    
    /*@PreAuthorize("hasAuthority('ticketing.add')")
    @PostMapping(value = "/submitTicket", consumes = "application/json", produces = "application/json")
//BCP-25viewEditTicket?id=189
    public QueryResultDTO ticketSubmit(@RequestBody TicketInsertion ticketInsertion) {
        return ticketService.submitTicket(ticketInsertion);
    }*/

    @PreAuthorize("hasAuthority('ticketing.add')")
    @PostMapping(value = "/submitTicket", consumes = "application/json", produces = "application/json")
    public CompletableFuture<QueryResultDTO> submitTicket(@RequestBody TicketInsertion ticketInsertion) {
        return ticketService.submitTicketFuture(ticketInsertion);
    }

    /**
     * Contact RMA：发送支持咨询邮件。
     *
     * 说明：
     * 1. 收件人固定为 RMAsupport@pax.us（由后端控制，避免被篡改）。
     * 2. 自动携带当前登录用户信息（姓名、组织、邮箱）。
     * 3. 支持可选截图附件（图片）。
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/contact-rma", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public QueryResultDTO contactRma(@RequestParam(value = "ticketId", required = false) String ticketId,
                                     @RequestParam("subject") String subject,
                                     @RequestParam("message") String message,
                                     @RequestPart(value = "screenshot", required = false) MultipartFile screenshot) {
        return ticketService.contactRma(ticketId, subject, message, screenshot);
    }

    /**
     * 上传工单附件。
     * 说明：
     * 1. 支持多文件上传。
     */
    @PreAuthorize("hasAnyAuthority('ticketing.add', 'ticketing.update')")
    @PostMapping(value = "/{ticketId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public QueryResultArrayDTO uploadTicketAttachments(@PathVariable("ticketId") Integer ticketId,
                                                       @RequestParam(value = "remark", required = false) String remark,
                                                       @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ticketService.uploadTicketAttachments(ticketId, remark, files);
    }

    /**
     * 查询工单附件列表（返回预签名下载 URL）。
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/{ticketId}/attachments")
    public QueryResultArrayDTO listTicketAttachments(@PathVariable("ticketId") Integer ticketId) {
        return ticketService.listTicketAttachments(ticketId);
    }

    /**
     * 获取单个附件预签名下载 URL。
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/{ticketId}/attachments/{fileId}/download-url")
    public QueryResultDTO generateAttachmentDownloadUrl(@PathVariable("ticketId") Integer ticketId,
                                                        @PathVariable("fileId") Integer fileId) {
        return ticketService.generateAttachmentDownloadUrl(ticketId, fileId);
    }

    /**
     * 删除工单附件。
     */
    @PreAuthorize("hasAnyAuthority('ticketing.update')")
    @DeleteMapping("/{ticketId}/attachments/{fileId}")
    public QueryResultDTO deleteTicketAttachment(@PathVariable("ticketId") Integer ticketId,
                                                 @PathVariable("fileId") Integer fileId) {
        return ticketService.deleteTicketAttachment(ticketId, fileId);
    }

    /**
     * 本地回退模式下，按 token 下载附件。
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/attachments/local/{token}")
    public void downloadLocalAttachment(@PathVariable("token") String token, HttpServletResponse response) throws IOException {
        ticketService.downloadLocalAttachment(token, response);
    }
    
    /**
     * Need to check if they user's id == ticket's cmoid OR it's pax employee
     *
     * @param id MO OID
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/{ticketId}")
    public QueryResultDTO viewTicket(@PathVariable(value = "ticketId", required = true) String id) {
        return ticketService.viewEditTicket(id);
    }

    /**
     * 获取工单邮件详情预览（与提交时发送邮件内容一致）
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/{ticketId}/email-preview")
    public QueryResultDTO getTicketEmailPreview(@PathVariable("ticketId") String ticketId) {
        return ticketService.getTicketEmailPreview(ticketId);
    }
    
    /**
     * BCP-28 view details, id is xm_oid
     * Need to check if they user's id == ticket's cmoid OR it's pax employee
     */
    @PreAuthorize("hasAuthority('ticketing.view')")
    @GetMapping("/viewDetails")
    public QueryResultArrayDTO viewTicketDetails(@RequestParam(value = "id") Integer id,
                                                 @RequestParam(value = "ticketId") Integer ticketId) {
        return ticketService.viewTicketDetails(id, ticketId);
    }
    
    /**
     * Edit ticket. Need to check if they user's cmid == ticket's cmoid or it's a pax Employee.
     */
    @PreAuthorize("hasAuthority('ticketing.update')")
    @PutMapping("/{ticketId}")
    public QueryResultArrayDTO updateTicket(@PathVariable("ticketId") String id, @RequestBody TicketEditDTO ticketEditDTO) {
        return ticketService.editTicket(id, ticketEditDTO);
    }
    
    /**
     * View Ticket for customers. Only return tickets under user's company id
     */
    @PreAuthorize("hasAuthority('ticketing.view')")
    @GetMapping("/viewTickets")
    public QueryResultArrayDTO viewTickets(@RequestParam(value = "page", required = false) Integer currentPage,
                                           @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                           @RequestParam(value = "sort", required = false) String sortColumns,
                                           @RequestParam(value = "ticketId", required = false) String ticketId,
                                           @RequestParam(value = "department", required = false) Integer department,
                                           @RequestParam(value = "responder", required = false) String responder,
                                           @RequestParam(value = "status", required = false) Integer status,
                                           @RequestParam(value = "type", required = false) Integer type,
                                           @RequestParam(value = "createdDate", required = false) String createdDate,
                                           @RequestParam(value = "lastResponse", required = false) String lastResponse,
                                           @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                           @RequestParam(value = "customerOrganization", required = false) String customerOrganization,
                                           @RequestParam(value = "customerId", required = false) String customerId) {
        
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }
        
        if (null == sizePerPage) {
            sizePerPage = 10;
        }
        
        return ticketService.ticketQueryViews(currentPage, sizePerPage, sortColumns, ticketId, department, responder, status, type, createdDate, lastResponse, serialNumber, customerOrganization, customerId);
    }
    
    /**
     * Ticket queue for pax employee. Need to check if user is pax employee.
     */
    @PreAuthorize("hasAuthority('ticketing.queue')")
    @GetMapping("/queue")
    public QueryResultArrayDTO status(@RequestParam(value = "page", required = false) Integer currentPage,
                                      @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                      @RequestParam(value = "sort", required = false) String sortColumns,
                                      @RequestParam(value = "ticketId", required = false) String ticketId,
                                      @RequestParam(value = "department", required = false) Integer department,
                                      @RequestParam(value = "responder", required = false) String responder,
                                      @RequestParam(value = "status", required = false) Integer status,
                                      @RequestParam(value = "type", required = false) Integer type,
                                      @RequestParam(value = "createdDate", required = false) String createdDate,
                                      @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                      @RequestParam(value = "customerId", required = false) String customerId,
                                      @RequestParam(value = "searchSubmitted", required = false) Integer searchSubmitted,
                                      @RequestParam(value = "acknowledged", required = false) Integer acknowledged
    ) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }
        
        if (null == sizePerPage) {
            sizePerPage = 10;
        }
        return ticketService.ticketQuery(currentPage, sizePerPage, sortColumns, ticketId, department, responder, status, type, createdDate, serialNumber, customerId, searchSubmitted, acknowledged);
    }
    
    /**
     * Message board in edit ticket. both pax employee and user under this company can see it.
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/{ticketId}/response")
    public QueryResultArrayDTO getResponsesForTicket(@PathVariable("ticketId") Long ticketId) {
        return ticketService.getResponse(String.valueOf(ticketId));
    }
    
    /**
     * Message board in edit ticket. both pax employee and user under this company can see it.
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @PostMapping("/{ticketId}/response")
    public QueryResultDTO addResponsesForTicket(@PathVariable("ticketId") Long ticketId, @RequestBody TicketResponse response) {
        return ticketService.insertResponse(ticketId, response);
    }
    
    /**
     * Message board in edit ticket. only pax employee can update ack status.
     */
    @PreAuthorize("hasAnyAuthority('ticketing.ack')")
    @PutMapping("/{ticketId}/acknowledged")
    public QueryResultDTO acknowledgeTicket(@PathVariable("ticketId") Long ticketId,
                                            @RequestParam("acknowledged") Integer acknowledged) {
        return ticketService.setTicketAckStatus(ticketId, acknowledged);
    }
    
    /**
     * Message board in edit ticket. client and pax employee can unack a ticket.
     */
    @PreAuthorize("hasAnyAuthority('ticketing.ack', 'ticketing.view', 'ticketing.update')")
    @PutMapping("/{ticketId}/unacknowledged")
    public QueryResultDTO unacknowledgeTicket(@PathVariable("ticketId") Long ticketId) {
        return ticketService.unAckTicket(ticketId);
    }
    
    /**
     * Message board in edit ticket. client and pax employee can get a ticket's ack status.
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/{ticketId}/acknowledged")
    public QueryResultDTO getTicketAckStatus(@PathVariable("ticketId") Long ticketId) {
        return ticketService.getTicketAckStatus(ticketId);
    }

    @PreAuthorize("hasAuthority('ticketing.update')")
    @PutMapping("/{ticketId}/close")
    public QueryResultDTO closePrepTicket(@PathVariable("ticketId") Long ticketId) {
        return ticketService.closePrepTicket(ticketId);
    }
    
    @GetMapping("/dropdown/department")
    public QueryResultArrayDTO departmentDropDown() {
        return ticketService.queryDepartment();
    }
    
    @GetMapping("/dropdown/order_type")
    public QueryResultArrayDTO orderTypeDropDown() {
        return ticketService.queryOrderType();
    }
    
    @GetMapping("/dropdown/status")
    public QueryResultArrayDTO statusDropDown() {
        return ticketService.queryStatus();
    }
    
    @GetMapping("/dropdown/repair_type") //BCP-25
    public QueryResultArrayDTO repairTypeDropDown() {
        return ticketService.queryRepairType();
    }
    
    /**
     * Get all keys
     */
    @GetMapping("/dropdown/key") //BCP-25
    public QueryResultArrayDTO keyDropDown() {
        return ticketService.queryKey();
    }
    
    
    /**
     * Get unique key type
     */
    @GetMapping("/dropdown/key_type") //BCP-25
    public QueryResultArrayDTO keyTypeDropDown() {
        return ticketService.queryKeyType();
    }
    
    /**
     * Get unique kcv using key_type
     */
    @GetMapping("/dropdown/kcv") //BCP-25
    public QueryResultArrayDTO kcvDropDown(@RequestBody Key key) {
        return ticketService.queryKeyKcv(key.getKeyType());
    }
    
    /**
     * Get unique ksi using key_type and kcv
     */
    @GetMapping("/dropdown/ksi") //BCP-25
    public QueryResultArrayDTO ksiDropDown(@RequestBody Key key) {
        return ticketService.queryKeyKsi(key.getKeyType(), key.getKcv());
    }
    
    @GetMapping("/dropdown/cust_org")
    public QueryResultArrayDTO customerOrgDropDown() {
        return ticketService.queryCustomerOrg();
    }
}
