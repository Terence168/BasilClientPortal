package us.pax.basil.exception;

import us.pax.basil.enums.ApiCodeEnum;

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
 * 2020/10/21 	         ly            	    
 * ============================================================================
 */
@SuppressWarnings("serial")
public class ApiException extends RuntimeException {
    private String code;

    public ApiException() {
        super();
    }

    public ApiException(ApiCodeEnum code){
        super(code.getMessage());
        setCode(code.getCode());
    }

    public ApiException(String code, String message) {
        super(message);
        setCode(code);
    }

    public ApiException(String code, String message, Throwable cause) {
        super(message, cause);
        setCode(code);
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
