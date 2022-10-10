package us.pax.basil.security;

import us.pax.basil.mapper.UserMapper;
import us.pax.basil.utils.ResponseUtil;
import com.paxcq.cloud.common.dto.Result;
import com.paxcq.cloud.common.util.HttpServletUtils;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/***
 * ============================================================================
 * = COPYRIGHT file
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/10/20 	         ly            	    
 * ============================================================================
 */
@Component
@Log4j2
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        log.info("User [{}] login successfully, IP: {}", userDetails.getUsername(), HttpServletUtils.getClientIp());
        
//        userMapper.setLastLogin(userDetails.getEmpOid());
        
        ResponseUtil.out(response, Result.ok());
    }
}
