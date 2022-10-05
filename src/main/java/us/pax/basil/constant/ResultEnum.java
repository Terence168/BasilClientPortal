package us.pax.basil.constant;
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
 * 2021/08/20               Jay.Zhou
 * ============================================================================
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultEnum {

    SUCCESS(20000, "Request Success"),
    SYSTEM_ERROR(30000, "System Error! Please Re-try"),
    SESSION_TIMEOUT(40000, "Session Expired! Please Login Again"),
    FORBIDDEN(40001, "Unauthorized! Access Denied"),
    BUSINESS_ERROR(50000, "Business Processing Exception"),
    INVALID_ARGUMENTS(50001, "Invalid Parameter"),
    INVALID_REQUEST_METHOD(30001, "Invalid Request Method");

    private final int code;
    private final String message;
}

