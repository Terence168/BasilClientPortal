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
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.rma.Quarantine;
import us.pax.basil.entity.rma.Shipped;
import us.pax.basil.entity.rma.StatusExcelExport;
import us.pax.basil.service.TicketService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


@Api(tags = "Basil API Interface")
@RestController //?
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/queue")
    public QueryResultArrayDTO status(@RequestParam(value = "page", required = false) Integer currentPage,
                                      @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                      @RequestParam(value = "sort", required = false) String sortColumns,
                                      @RequestParam(value = "ticketId", required = false) String ticketId,
                                      @RequestParam(value = "department", required = false) String department,
                                      @RequestParam(value = "responder", required = false) String responder,
                                      @RequestParam(value = "status", required = false) String status,
                                      @RequestParam(value = "type", required = false) String type,
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
}
