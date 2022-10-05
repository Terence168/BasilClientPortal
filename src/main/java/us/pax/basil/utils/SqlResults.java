package us.pax.basil.utils;
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
 * 2021/05/10               rb
 * ============================================================================
 */

public abstract class SqlResults {
    public int resultCode=0;
    public String errorMessage="";
    
    public void setResultCode(int value) {
        resultCode = value;
    }
    public int getResultCode() {
        return resultCode;
    }
    public void setErrorMessage(String value) {
        errorMessage = value;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
