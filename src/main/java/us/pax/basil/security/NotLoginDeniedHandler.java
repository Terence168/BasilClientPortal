package us.pax.basil.security;

import us.pax.basil.utils.ResponseUtil;
import com.paxcq.cloud.common.dto.Result;
import com.paxcq.cloud.common.enums.ResultEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
 * Date	                 Author	                Action
 * 2020/10/20 	         ly            	    
 * ============================================================================
 */
@Log4j2
public class NotLoginDeniedHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.error("Un-logged in accessing authorized resource: {}", httpServletRequest.getRequestURI());
        ResponseUtil.out(httpServletResponse, Result.error(ResultEnum.SESSION_TIMEOUT));
    }
}
