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
import org.springframework.web.bind.annotation.*;

import us.pax.basil.dto.output.QueryResultArrayDTO;

import us.pax.basil.dto.output.SubmitTicketDTO;
import us.pax.basil.entity.ticket.TicketInsertion;
import us.pax.basil.service.TicketService;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityManager;

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
    @PostMapping(value = "/submitTicket", consumes = "application/json", produces = "application/json")//BCP-25
    //submit the ticket
    public SubmitTicketDTO ticketSubmit(@RequestBody TicketInsertion ticketInsertion){
        return ticketService.submitTicket(ticketInsertion);
    }

    @GetMapping("/viewTicketDetails")//BCP-28
    public QueryResultArrayDTO viewTicketDetails(@RequestParam(value = "id", required = true) String id){
        return ticketService.viewTicketDetails(id);
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

}
