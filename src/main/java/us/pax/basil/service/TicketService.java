package us.pax.basil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.ticket.SubmittingTicket;
import us.pax.basil.entity.ticket.TicketInsertionObject;

import javax.persistence.EntityManager;
import java.util.List;

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
    QueryResultArrayDTO queryDepartment();
    QueryResultArrayDTO queryOrderType();
    QueryResultArrayDTO queryStatus();
    QueryResultArrayDTO queryRepairType();
    QueryResultArrayDTO batchSerialNumberQuery(EntityManager entityManager, MultipartFile file, String fileName);
    QueryResultArrayDTO serialNumberQuery(String serialNumber);
    int insertTicketToPMO(TicketInsertionObject tio);
}
