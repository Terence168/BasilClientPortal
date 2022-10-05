package us.pax.basil.enums;

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

public enum ApiCodeEnum {
    SUCCESS("000000", "Success"),

    SYSTEM_ERROR("100000", "System Error"),
    FORMAT_ERROR("100001", "Request Format Error"),
    ARGUMENTS_ERROR("100002", "Parameter Error"),
    REQUEST_METHOD_ERROR("100003", "Request Method Error"),
    CERT_ERROR("100004", "Client Cert Error"),
    CLIENT_ACCESS_ERROR("100005", "Invalid Access ID"),
    SIGNATURE_ERROR("100006", "Invalid Signature");

    private String code;

    private String message;

    ApiCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
