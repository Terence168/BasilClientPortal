package us.pax.basil.entity.ticket;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

/**
 * Contact RMA 邮件消息落库实体。
 * 对应表：BASIL_SEC_PRD.EMAIL_MESSAGES
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmailMessage {

    /**
     * 主键：邮件消息ID
     */
    private Integer emOid;

    /**
     * 工单号：MO_OID
     */
    private Integer moOid;

    /**
     * 提交用户ID：U_OID
     */
    private Integer uOid;

    /**
     * 发送时间：SENT_TIME
     */
    private Timestamp sentTime;

    /**
     * 消息主题
     */
    private String messageSubject;

    /**
     * 消息正文
     */
    private String messageBody;
}

