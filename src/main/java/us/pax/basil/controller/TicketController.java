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
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import us.pax.basil.dto.output.*;

import us.pax.basil.entity.ticket.*;
import us.pax.basil.service.TicketService;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public QueryResultArrayDTO serialNumberUpdate(@RequestParam(value = "serialNumber", required = true) String serialNumber){
        return ticketService.serialNumberQuery(serialNumber);
    }
    //upload Excel file, process serial number by batch processing.
    @PreAuthorize("hasAnyAuthority('ticketing.add', 'ticketing.update')")
    @PostMapping("/batchSerialNumberQuery")
    public QueryResultArrayDTO batchSerialNumberUpload(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("fileName") String fileName
                                                      ){
        return ticketService.batchSerialNumberQuery(entityManager, file, fileName);
    }
    //submit the ticket
    @PreAuthorize("hasAuthority('ticketing.add')")
    @PostMapping(value = "/submitTicket", consumes = "application/json", produces = "application/json")//BCP-25viewEditTicket?id=189
    public QueryResultDTO ticketSubmit(@RequestBody TicketInsertion ticketInsertion){
        return ticketService.submitTicket(ticketInsertion);
    }

    /**
     * Need to check if they user's id == ticket's cmoid OR it's pax employee
     * @param id MO OID
     */
    @PreAuthorize("hasAnyAuthority('ticketing.view', 'ticketing.update')")
    @GetMapping("/{ticketId}")
    public QueryResultDTO viewTicket(@PathVariable(value = "ticketId", required = true) String id){
        return ticketService.viewEditTicket(id);
    }

    /**
     * BCP-28 view details, id is xm_oid
     * Need to check if they user's id == ticket's cmoid OR it's pax employee
     * */
    @PreAuthorize("hasAuthority('ticketing.view')")
    @GetMapping("/viewDetails")
    public QueryResultArrayDTO viewTicketDetails(@RequestParam(value = "id", required = true) Integer id){
        return ticketService.viewTicketDetails(id);
    }

    /**
     * Edit ticket. Need to check if they user's cmid == ticket's cmoid or it's a pax Employee.
     */
    @PreAuthorize("hasAuthority('ticketing.update')")
    @PutMapping("/{ticketId}")
    public QueryResultArrayDTO updateTicket(@PathVariable("ticketId") String id, @RequestBody TicketEditDTO ticketEditDTO){
        return ticketService.editTicket(id, ticketEditDTO);
    }

    /**
     * View Ticket for customers. Only return tickets under user's company id
     * */
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
                                                       @RequestParam(value = "customerId", required = false) String customerId){

        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if(null == sizePerPage){
            sizePerPage = 10;
        }

        return ticketService.ticketQueryViews(currentPage, sizePerPage, sortColumns, ticketId, department,responder, status, type, createdDate,lastResponse, serialNumber,customerOrganization,customerId);
    }

    /**
     * Ticket queue for pax employee. Need to check if user is pax employee.
     * */
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
                                      @RequestParam(value = "customerId", required = false) String customerId
                                      ){
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if(null == sizePerPage){
            sizePerPage = 10;
        }
        return ticketService.ticketQuery(currentPage, sizePerPage, sortColumns, ticketId, department,responder, status, type, createdDate, serialNumber, customerId);
    }

    /**
     * Message board in edit ticket. both pax employee and user under this company can see it.
     * */
    @PreAuthorize("hasAnyAuthority('ticketing.update')")
//    @PreAuthorize("hasAuthority('ticketing.update')")
    @GetMapping("/{ticketId}/response")
    public QueryResultArrayDTO getResponsesForTicket(@PathVariable("ticketId") Long ticketId) {
        return ticketService.getResponse(String.valueOf(ticketId));
    }

    /**
     * Message board in edit ticket. both pax employee and user under this company can see it.
     * */
    @PreAuthorize("hasAnyAuthority('ticketing.update')")
    @PostMapping("/{ticketId}/response")
    public QueryResultDTO addResponsesForTicket(@PathVariable("ticketId") Long ticketId, @RequestBody TicketResponse response) {
        return ticketService.insertResponse(response);
    }

    /**
     * Message board in edit ticket. only pax employee can ack a ticket.
     * */
    @PreAuthorize("hasAnyAuthority('ticketing.ack')")
    @PutMapping("/{ticketId}/acknowledged")
    public QueryResultDTO acknowledgeTicket(@PathVariable("ticketId")Long ticketId){
        return ticketService.ackTicket(ticketId);
    }

    /**
     * Message board in edit ticket. client and pax employee can unack a ticket.
     * */
    @PreAuthorize("hasAnyAuthority('ticketing.unack')")
    @PutMapping("/{ticketId}/unacknowledged")
    public QueryResultDTO unacknowledgeTicket(@PathVariable("ticketId")Long ticketId){
        return ticketService.unAckTicket(ticketId);
    }

    /**
     * Message board in edit ticket. client and pax employee can get a ticket's ack status.
     * */
    @PreAuthorize("hasAnyAuthority('ticketing.update')")
    @GetMapping("/{ticketId}/acknowledged")
    public QueryResultDTO getTicketAckStatus(@PathVariable("ticketId")Long ticketId){
        return ticketService.getTicketAckStatus(ticketId);
    }

    @GetMapping("/dropdown/department")
    public QueryResultArrayDTO departmentDropDown(){
        return ticketService.queryDepartment();
    }
    @GetMapping("/dropdown/order_type")
    public QueryResultArrayDTO orderTypeDropDown(){
        return ticketService.queryOrderType();
    }
    @GetMapping("/dropdown/status")
    public QueryResultArrayDTO statusDropDown(){
        return ticketService.queryStatus();
    }
    @GetMapping("/dropdown/repair_type") //BCP-25
    public QueryResultArrayDTO repairTypeDropDown(){
        return ticketService.queryRepairType();
    }

    @GetMapping("/dropdown/key_type") //BCP-25
    public QueryResultArrayDTO keyTypeDropDown(){
        return ticketService.queryKeyType();
    }

    @GetMapping("/dropdown/cust_org")
    public QueryResultArrayDTO customerOrgDropDown(){
        return ticketService.queryCustomerOrg();
    }
}
