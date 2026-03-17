package us.pax.basil.entity.ticket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 工单附件存储实体。
 *
 * 说明：
 * 1. 该实体对应 BASIL_SEC_PRD.MASTER_RMA_FILE_STORAGE 表。
 * 2. PATH 字段统一保存“对象键（object key）/相对路径”，
 *    便于在 S3 与本地目录两种模式下共用同一份数据库记录。
 * 3. SIZE 在库中定义为 varchar(10)，因此这里使用 String 做映射，
 *    写入时由业务层将字节数转为字符串。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RmaFileStorage {
    /**
     * 主键：附件记录 OID。
     */
    private Integer mrfOID;

    /**
     * 工单号（MO_OID）。
     */
    private Integer moOID;

    /**
     * 原始文件名。
     */
    private String fileName;

    /**
     * 存储路径（S3 object key 或本地相对路径）。
     */
    private String path;

    /**
     * 文件大小（字节字符串）。
     */
    private String size;

    /**
     * 文件类型（MIME）。
     */
    private String type;
}

