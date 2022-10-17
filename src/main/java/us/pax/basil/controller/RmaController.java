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


import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.service.RmaService;
import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Basil API Interface")
@RestController
@RequestMapping("/rma")
public class RmaController {

    @Autowired
    private RmaService rmaService;

    // @PreAuthorize("hasAuthority('admin.user.delete')")
    @GetMapping("/status")
    public QueryResultArrayDTO status(@RequestParam(value = "page", required = false) Integer currentPage,
                                       @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                       @RequestParam(value = "sort", required = false) String sortColumns,
                                       @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                       @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                       @RequestParam(value = "partNumber", required = false) String partNumber) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        return rmaService.statusQuery(currentPage, sizePerPage, sortColumns, rmaNumber, serialNumber, partNumber);
    }


    // @PreAuthorize("hasAuthority('admin.user.delete')")
    @GetMapping("/shipped")
    public QueryResultArrayDTO shipped (@RequestParam(value = "page", required = false) Integer currentPage,
                                         @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                         @RequestParam(value = "sort", required = false) String sortColumns,
                                         @RequestParam(value = "rma", required = false) Long rmaNumber,
                                         @RequestParam(value = "serial", required = false) String serialNumber,
                                         @RequestParam(value = "partNumber", required = false) String partNumber) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        return rmaService.shippedQuery(currentPage, sizePerPage, sortColumns, rmaNumber, serialNumber, partNumber);
    }

    // @PreAuthorize("hasAuthority('admin.user.delete')")
    @GetMapping("/quarantine")
    public QueryResultArrayDTO quarantine(@RequestParam(value = "page", required = false) Integer currentPage,
                                           @RequestParam(value = "per_page", required = false) Integer sizePerPage,
                                           @RequestParam(value = "sort", required = false) String sortColumns,
                                           @RequestParam(value = "rmaNumber", required = false) Long rmaNumber,
                                           @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                           @RequestParam(value = "partNumber", required = false) String partNumber) {
        if (null == currentPage || 0 == currentPage) {
            currentPage = 1; // show the first page by default
        }

        if (null == sizePerPage) {
            sizePerPage = 10; // show 10 items per page by default
        }
        return rmaService.quarantineQuery(currentPage, sizePerPage, sortColumns, rmaNumber, serialNumber, partNumber);
    }
}
