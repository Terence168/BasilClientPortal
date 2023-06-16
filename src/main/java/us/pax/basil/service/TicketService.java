package us.pax.basil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import us.pax.basil.dto.output.QueryResultArrayDTO;

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
 * 2021/05/10               rb
 * ============================================================================
 */
public interface TicketService extends IService<Integer>{
    QueryResultArrayDTO ticketQuery(Integer currentPage,
                                     Integer sizePerPage,
                                     String sortColumns,
                                     String ticketId,
                                     Integer department,
                                     String responder,
                                     Integer status,
                                     Integer type,
                                     String createdDate,
                                     String serialNumber,
                                     String customerId);
    QueryResultArrayDTO queryDepartment(Integer department);
    QueryResultArrayDTO queryOrderType(Integer orderType);
    QueryResultArrayDTO queryStatus(Integer status);
}
