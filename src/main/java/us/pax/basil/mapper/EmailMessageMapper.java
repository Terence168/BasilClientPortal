package us.pax.basil.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import us.pax.basil.entity.ticket.EmailMessage;

/**
 * Contact RMA 邮件消息 Mapper。
 */
public interface EmailMessageMapper extends BaseMapper<EmailMessage> {

    /**
     * 新增邮件消息记录。
     */
    Integer insertEmailMessage(EmailMessage emailMessage);
}

