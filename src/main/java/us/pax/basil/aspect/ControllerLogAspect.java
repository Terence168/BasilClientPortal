package us.pax.basil.aspect;

import com.paxcq.cloud.common.util.HttpServletUtils;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.utils.SqlResults;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

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
    private static final Set<String> LIGHT_LOG_ENDPOINT_SUFFIXES = new HashSet<>();

    static {
        LIGHT_LOG_ENDPOINT_SUFFIXES.add("/email-preview");
        LIGHT_LOG_ENDPOINT_SUFFIXES.add("/queue");
        LIGHT_LOG_ENDPOINT_SUFFIXES.add("/viewTickets");
        LIGHT_LOG_ENDPOINT_SUFFIXES.add("/dropdown/status");
    }

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
        String requestUri = httpServletRequest.getRequestURI();
        boolean lightLog = shouldUseLightLog(requestUri);

        log.info("============================================================================");
        log.info("Request Resource: {}", requestUri);
        log.info("Request Method: {}", httpServletRequest.getMethod());
        log.info("Request Param: {}", httpServletRequest.getParameterMap());

        if(null != userPrincipal){
            log.info("Request User: {}", userPrincipal.getName());
        }

        log.info("Entering Processing Method: [{}]", method.getName());
        log.info("");
        Object result = pjp.proceed();
        log.info("");
        if (lightLog) {
            log.info("API Response: {}", buildCompactResponseLog(result));
        } else {
            log.info("API Response: {}", result);
        }
        long costTime = System.currentTimeMillis() - startTime;
        log.info("API [{}] Process Completed, Time Consumed: {}.{} s", method.getName(), costTime/1000, String.format("%03d", costTime % 1000));
        log.info("============================================================================");
        return result;
    }

    private boolean shouldUseLightLog(String requestUri) {
        if (requestUri == null || requestUri.trim().isEmpty()) {
            return false;
        }
        for (String suffix : LIGHT_LOG_ENDPOINT_SUFFIXES) {
            if (requestUri.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private String buildCompactResponseLog(Object result) {
        if (result == null) {
            return "null";
        }

        if (result instanceof QueryResultArrayDTO) {
            QueryResultArrayDTO response = (QueryResultArrayDTO) result;
            int size = response.getData() == null ? 0 : response.getData().size();
            return String.format(
                    "QueryResultArrayDTO(resultCode=%d, total=%d, dataSize=%d, errorMessage=%s)",
                    response.getResultCode(),
                    response.getTotal(),
                    size,
                    response.getErrorMessage()
            );
        }

        if (result instanceof QueryResultDTO) {
            QueryResultDTO response = (QueryResultDTO) result;
            int size = response.getData() == null ? 0 : response.getData().size();
            return String.format(
                    "QueryResultDTO(resultCode=%d, dataSize=%d, errorMessage=%s)",
                    response.getResultCode(),
                    size,
                    response.getErrorMessage()
            );
        }

        if (result instanceof SqlResults) {
            SqlResults response = (SqlResults) result;
            return String.format(
                    "SqlResults(resultCode=%d, errorMessage=%s, type=%s)",
                    response.getResultCode(),
                    response.getErrorMessage(),
                    result.getClass().getSimpleName()
            );
        }

        return result.getClass().getSimpleName();
    }
}
