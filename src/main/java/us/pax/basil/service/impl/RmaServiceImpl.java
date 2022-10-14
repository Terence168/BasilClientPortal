package us.pax.basil.service.impl;


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
* 2020/04/24               yinyy
* ============================================================================
*/

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import us.pax.basil.constant.PasswordConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.entity.rma.Shipped;
import us.pax.basil.mapper.PasswordMapper;
import us.pax.basil.mapper.RmaMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.property.MailProperties;
import us.pax.basil.service.PasswordService;
import us.pax.basil.service.RmaService;
import us.pax.basil.utils.EmailUtil;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RmaServiceImpl extends ServiceImpl<RmaMapper, Integer> implements RmaService {
    private RmaMapper rmaMapper;

	@Override
	public QueryResultArrayDTO statusQuery(Integer currentPage, Integer sizePerPage, String sortColumns, Long rmaNumber,
			String serialNumber, String partNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResultArrayDTO shippedQuery(Integer currentPage, Integer sizePerPage, String sortColumns,
			Long rmaNumber, String serialNumber, String partNumber) {
		List<Shipped> shippedList = rmaMapper.getShipped(currentPage, sizePerPage, sortColumns, rmaNumber, currentPage, sizePerPage);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResultArrayDTO quarantineQuery(Integer currentPage, Integer sizePerPage, String sortColumns,
			Long rmaNumber, String serialNumber, String partNumber) {
		// TODO Auto-generated method stub
		return null;
	}
}
