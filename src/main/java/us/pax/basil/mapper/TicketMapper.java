package us.pax.basil.mapper;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import us.pax.basil.entity.ticket.*;
public interface TicketMapper extends BaseMapper<Integer> {
    List<TicketingQueue> getTicketing(Integer offset,
                                      Integer count,
                                      String sortColumns,
                                      String id,
                                      String[] ticketId,//suppose it should be multiple searching
                                      String department,
                                      String status,
                                      String responder,
                                      String[] serialNumber,
                                      String createdDate);
}
