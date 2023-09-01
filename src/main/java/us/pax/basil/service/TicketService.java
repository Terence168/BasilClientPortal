package us.pax.basil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.RequestParam;
import us.pax.basil.dto.output.*;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.entity.ticket.*;

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
                                     String customerOrganization);

    QueryResultArrayDTO ticketQueryViews(Integer currentPage,
                                    Integer sizePerPage,
                                    String sortColumns,
                                    String ticketId,
                                    Integer department,
                                    String responder,
                                    Integer status,
                                    Integer type,
                                    String createdDate,
                                    String lastResponse,
                                    String serialNumber,
                                    String customerOrganization,String customerId);


    QueryResultArrayDTO viewTicketDetails(Integer id);
    QueryResultArrayDTO queryDepartment();
    QueryResultArrayDTO queryOrderType();
    QueryResultArrayDTO queryStatus();
    QueryResultArrayDTO queryRepairType();
    QueryResultArrayDTO queryKeyType();
    QueryResultArrayDTO batchSerialNumberQuery(EntityManager entityManager, MultipartFile file, String fileName);
    QueryResultArrayDTO serialNumberQuery(String serialNumber);
    int insertTicketToPMO(TicketInsertionObject tio);
    QueryResultDTO submitTicket(TicketInsertion ticketInsertion);
    QueryResultDTO viewEditTicket(String id);
    QueryResultDTO insertResponse(TicketResponse ticketResponse);
    QueryResultArrayDTO getResponse(String id);
    QueryResultArrayDTO editTicket(String id, TicketEditDTO ticketEditDTO);
    QueryResultDTO ackTicket(Long moOID);
    QueryResultDTO unAckTicket(Long moOID);

}
