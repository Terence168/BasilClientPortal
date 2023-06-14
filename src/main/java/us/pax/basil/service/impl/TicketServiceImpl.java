package us.pax.basil.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.ticket.*;
import us.pax.basil.mapper.TicketMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.TicketService;
import us.pax.basil.utils.AuthUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Integer> implements TicketService {

    private TicketMapper ticketMapper;
    @Override
    public QueryResultArrayDTO ticketQuery(Integer currentPage,
                                    Integer sizePerPage,
                                    String sortColumns,
                                    String ticketId,
                                    String department,
                                    String responder,
                                    String status,
                                    String type,
                                    String createdDate,
                                    String serialNumber,
                                    String customerId){

        return null;
    }
}
