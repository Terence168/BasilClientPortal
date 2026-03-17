package us.pax.basil.mapper;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import us.pax.basil.entity.ticket.RmaFileStorage;

import java.util.List;

/**
 * 工单附件存储表 Mapper。
 *
 * 说明：
 * 1. 负责 BASIL_SEC_PRD.MASTER_RMA_FILE_STORAGE 的增删查。
 * 2. 附件下载地址（预签名 URL）由业务层动态生成，不落库。
 */
public interface RmaFileStorageMapper extends BaseMapper<RmaFileStorage> {

    /**
     * 新增附件记录。
     */
    Integer insertRmaFileStorage(RmaFileStorage fileStorage);

    /**
     * 根据工单号查询附件列表。
     */
    List<RmaFileStorage> selectByTicketId(@Param("ticketId") Integer ticketId);

    /**
     * 按主键查询附件记录。
     */
    RmaFileStorage selectByFileId(@Param("fileId") Integer fileId);

    /**
     * 按工单号 + 附件主键查询，用于权限与归属校验。
     */
    RmaFileStorage selectByTicketIdAndFileId(@Param("ticketId") Integer ticketId, @Param("fileId") Integer fileId);

    /**
     * 按主键删除附件记录。
     */
    Integer deleteByFileId(@Param("fileId") Integer fileId);
}
