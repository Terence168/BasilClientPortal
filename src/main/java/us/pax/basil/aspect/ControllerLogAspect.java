package us.pax.basil.aspect;

import com.paxcq.cloud.common.util.HttpServletUtils;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.security.Principal;

/***
 * ============================================================================
 * = COPYRIGHT auth-web
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2020-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                     Author                    Action
 * 2020/8/14              ly                    
 * ============================================================================
 */
@Aspect
@Component
@Log4j2
public class ControllerLogAspect {
    @Pointcut("execution(public * us.pax.basil.controller.*.*(..))")
    private void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable{
        HttpServletRequest httpServletRequest = HttpServletUtils.getHttpRequest();
        Principal userPrincipal = httpServletRequest.getUserPrincipal();
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        log.info("============================================================================");
        log.info("Request Resource: {}", httpServletRequest.getRequestURI());
        log.info("Request Method: {}", httpServletRequest.getMethod());
        log.info("Request Param: {}", httpServletRequest.getParameterMap());

        if(null != userPrincipal){
            log.info("Request User: {}", userPrincipal.getName());
        }

        log.info("Entering Processing Method: [{}]", method.getName());
        log.info("");
        Object result = pjp.proceed();
        log.info("");
        log.info("API Response: {}", result);
        long costTime = System.currentTimeMillis() - startTime;
        log.info("API [{}] Process Completed, Time Consumed: {}.{} s", method.getName(), costTime/1000, String.format("%03d", costTime % 1000));
        log.info("============================================================================");
        return result;
    }
}
