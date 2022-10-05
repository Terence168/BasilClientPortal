package us.pax.basil.dto.output;

import us.pax.basil.enums.ApiCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

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
@Data
@AllArgsConstructor
public class ApiResultDTO {
    private String ResultCode;
    private String ResultMsg;

    public ApiResultDTO(ApiCodeEnum code){
        setResultCode(code.getCode());
        setResultMsg(code.getMessage());
    }
}
