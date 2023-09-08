package us.pax.basil.handler;

import us.pax.basil.constant.ResultEnum;
import us.pax.basil.dto.output.ApiResultDTO;
import us.pax.basil.enums.ApiCodeEnum;
import us.pax.basil.exception.ApiException;
import com.paxcq.cloud.common.dto.Result;
import com.paxcq.cloud.common.exception.AuthorizationException;
import com.paxcq.cloud.common.exception.BusinessException;
import com.paxcq.cloud.common.exception.RemoteException;
import com.paxcq.cloud.common.util.HttpServletUtils;
import com.paxcq.cloud.common.util.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/***
 * ============================================================================
 * = COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/04/26 	         yinyy            	    Global Exception Handler
 * ============================================================================
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     *
     * @param ex
     * @return
     * @Description Data transactions based on method level.
     *              1. add @Validated on the controller which need to be validated
     *              2. Add notation for the method parameters
     */
    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public Object handleApiConstraintViolationException(Throwable ex) {
        log.error(ex.getMessage(), ex);
        String requestUri = HttpServletUtils.getHttpRequest().getRequestURI();

//        if(requestUri.startsWith("/api/")){
//            return apiResultWrapper(ex);
//        }else {
//            return webResultWrapper(ex);
//        }
        
        return webResultWrapper(ex);
    }

    private String getConstraintViolationExceptionMessage(Throwable e) {
        ConstraintViolationException cve = (ConstraintViolationException) e;
        StringBuilder sb = new StringBuilder();
        Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            sb.append(violation.getMessage()).append(", ");
        }

        return sb.toString();
    }

    private Result<String> webResultWrapper(Throwable ex){
        if (ex instanceof ConstraintViolationException) {
            return Result.error(ResultEnum.INVALID_ARGUMENTS.getCode(), getConstraintViolationExceptionMessage(ex));
        } else if (ex instanceof AuthorizationException) {
            // Authorization Exception
            return Result.error(ResultEnum.FORBIDDEN.getCode(), ResultEnum.FORBIDDEN.getMessage());
        } else if (ex instanceof RemoteException) {
            // RPC Exception
            return Result.error(ResultEnum.BUSINESS_ERROR.getCode(), ResultEnum.BUSINESS_ERROR.getMessage());
        } else if (ex instanceof IllegalArgumentException) {
            // Parameter Error Exception
            return Result.error(ResultEnum.INVALID_ARGUMENTS.getCode(), ex.getMessage());
        } else if(ex instanceof HttpMessageNotReadableException){
            // Message Parsing Exception
            return Result.error(ResultEnum.INVALID_ARGUMENTS.getCode(), ResultEnum.INVALID_ARGUMENTS.getMessage());
        } else if(ex instanceof MethodArgumentNotValidException){
            // Parameter Validation Exception
            return Result.error(ResultEnum.INVALID_ARGUMENTS.getCode(), ValidateUtils.getErrorMessage(((MethodArgumentNotValidException)ex).getBindingResult()));
        } else if (ex instanceof BusinessException) {
            // Business Processing Exception
            return Result.error(ResultEnum.BUSINESS_ERROR.getCode(), ex.getMessage());
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            // Request Method Exception
            return Result.error(ResultEnum.INVALID_REQUEST_METHOD.getCode(), ResultEnum.INVALID_REQUEST_METHOD.getMessage());
        } else if (ex instanceof AccessDeniedException) {
            // Access Denied Exception
            return Result.error(ResultEnum.FORBIDDEN.getCode(), ResultEnum.FORBIDDEN.getMessage());
        } else {
            // Unknown Error
            return Result.error(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMessage());
        }
    }

    private ApiResultDTO apiResultWrapper(Throwable ex){

        if (ex instanceof ConstraintViolationException) {
            return new ApiResultDTO(ApiCodeEnum.ARGUMENTS_ERROR.getCode(), getConstraintViolationExceptionMessage(ex));
        } else if (ex instanceof IllegalArgumentException) {
            // Parameter Error Exception
            return new ApiResultDTO(ApiCodeEnum.ARGUMENTS_ERROR);
        } else if(ex instanceof HttpMessageNotReadableException){
            // Message Parsing Exception
            return new ApiResultDTO(ApiCodeEnum.FORMAT_ERROR);
        } else if(ex instanceof MethodArgumentNotValidException){
            // Parameter Validation Exception
            return new ApiResultDTO(ApiCodeEnum.ARGUMENTS_ERROR);
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            // Request Method Exception
            return new ApiResultDTO(ApiCodeEnum.REQUEST_METHOD_ERROR);
        } else if (ex instanceof ApiException) {
            ApiException apiEx = (ApiException)ex;
            return new ApiResultDTO(apiEx.getCode(), ex.getMessage());
        } else {
            // Unknown Error
            return new ApiResultDTO(ApiCodeEnum.SYSTEM_ERROR);
        }
    }
}
