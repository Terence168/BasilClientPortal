package us.pax.basil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.entity.ticket.TicketEditDTO;
import us.pax.basil.entity.ticket.TicketInsertion;
import us.pax.basil.entity.ticket.TicketInsertionObject;
import us.pax.basil.entity.ticket.TicketResponse;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
                                     String customerOrganization,
                                     Integer searchSubmitted,
                                     Integer acknowledged);

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


    QueryResultArrayDTO viewTicketDetails(Integer id, Integer ticketId);
    QueryResultArrayDTO queryDepartment();
    QueryResultArrayDTO queryOrderType();
    QueryResultArrayDTO queryStatus();
    QueryResultArrayDTO queryRepairType();
    QueryResultArrayDTO queryKeyType();
    QueryResultArrayDTO batchSerialNumberQuery(EntityManager entityManager, MultipartFile file, String fileName);
    QueryResultArrayDTO serialNumberQuery(String serialNumber);
    int insertTicketToPMO(TicketInsertionObject tio);
    QueryResultDTO submitTicket(TicketInsertion ticketInsertion);
    CompletableFuture<QueryResultDTO> submitTicketFuture(TicketInsertion ticketInsertion);
    QueryResultDTO viewEditTicket(String id);
    QueryResultDTO getTicketEmailPreview(String ticketId);
    QueryResultDTO insertResponse(Long ticketId, TicketResponse ticketResponse);
    QueryResultArrayDTO getResponse(String id);
    QueryResultArrayDTO editTicket(String id, TicketEditDTO ticketEditDTO);

    QueryResultDTO setTicketAckStatus(Long moOID, Integer acknowledged);
    QueryResultDTO ackTicket(Long moOID);
    QueryResultDTO unAckTicket(Long moOID);
    QueryResultDTO getTicketAckStatus(Long moOID);

    QueryResultArrayDTO queryCustomerOrg();

    QueryResultArrayDTO queryKeyKcv(String keyType);

    QueryResultArrayDTO queryKeyKsi(String keyType, String kcv);

    QueryResultArrayDTO queryKey();

    /**
     * 上传工单附件并记录文件元数据。
     * @param ticketId 工单号
     * @param remark 备注（可选）
     * @param files 多文件列表（可选）
     */
    QueryResultArrayDTO uploadTicketAttachments(Integer ticketId, String remark, List<MultipartFile> files);

    /**
     * 查询工单附件列表（包含预签名下载 URL）。
     */
    QueryResultArrayDTO listTicketAttachments(Integer ticketId);

    /**
     * 获取单个附件预签名下载 URL。
     */
    QueryResultDTO generateAttachmentDownloadUrl(Integer ticketId, Integer fileId);

    /**
     * 删除工单附件（包含对象存储与数据库记录）。
     */
    QueryResultDTO deleteTicketAttachment(Integer ticketId, Integer fileId);

    /**
     * 本地回退模式下，按 token 下载文件。
     */
    void downloadLocalAttachment(String token, HttpServletResponse response) throws IOException;

    /**
     * Contact RMA：发送工单咨询邮件（支持图片附件）。
     * @param ticketId 工单号（可空）
     * @param subject 主题
     * @param message 内容
     * @param screenshot 截图附件（可空）
     */
    QueryResultDTO contactRma(String ticketId, String subject, String message, MultipartFile screenshot);

}
