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
    @GetMapping("/serialNumberUpdate")//BCP-25
    //search serial number and return device information and repair price.
    public QueryResultArrayDTO serialNumberUpdate(@RequestParam(value = "serialNumber", required = true) String serialNumber){
        return ticketService.serialNumberQuery(serialNumber);
    }
    @PostMapping("/batchSerialNumberQuery")//BCP-25
    //upload Excel file, process serial number by batch processing.
    public QueryResultArrayDTO batchSerialNumberUpload(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("fileName") String fileName
                                                      ){
        return ticketService.batchSerialNumberQuery(entityManager, file, fileName);
    }
    @PostMapping(value = "/submitTicket", consumes = "application/json", produces = "application/json")//BCP-25viewEditTicket?id=189
    //submit the ticket
    public QueryResultDTO ticketSubmit(@RequestBody TicketInsertion ticketInsertion){
        return ticketService.submitTicket(ticketInsertion);
    }

    //get ticket
    @GetMapping("/{ticketId}")
    public QueryResultDTO viewEditTicket(@PathVariable(value = "ticketId", required = true) String id){
        return ticketService.viewEditTicket(id);
    }

    @GetMapping("/viewDetails")//BCP-28 view details, id is xm_oid
    public QueryResultArrayDTO viewTicketDetails(@RequestParam(value = "id", required = true) Integer id){
        return ticketService.viewTicketDetails(id);
    }

    @PostMapping("/editTicket/{ticketId}")
    public QueryResultArrayDTO updateTicketDetails(@PathVariable("ticketId") String id, @RequestBody TicketEditDTO ticketEditDTO){
        return ticketService.editTicket(id, ticketEditDTO);
    }

    // bcp 26
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

    @GetMapping("/{ticketId}/response")
    public QueryResultArrayDTO getResponsesForTicket(@PathVariable("ticketId") Long ticketId) {
        return ticketService.getResponse(String.valueOf(ticketId));
    }

    @PostMapping("/{ticketId}/response")
    public QueryResultDTO getResponsesForTicket(@PathVariable("ticketId") Long ticketId, @RequestBody TicketResponse response) {
        return ticketService.insertResponse(response);
    }

    @PreAuthorize("hasAuthority('ticketing.edit.acknowledge')")
    @PutMapping("/{ticketId}/acknowledged")
    public QueryResultDTO acknowledgeTicket(@PathVariable("ticketId")Long ticketId){
        return ticketService.ackTicket(ticketId);
    }


    @PutMapping("/{ticketId}/unacknowledged")
    public QueryResultDTO unacknowledgeTicket(@PathVariable("ticketId")Long ticketId){
        return ticketService.unAckTicket(ticketId);
    }

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

}
