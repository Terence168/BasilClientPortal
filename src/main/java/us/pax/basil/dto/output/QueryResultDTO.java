package us.pax.basil.dto.output;
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

import java.util.Map;
import us.pax.basil.utils.SqlResults;

public class QueryResultDTO extends SqlResults{
    private Map<String, Integer> data;
    
    public QueryResultDTO(Map<String, Integer> data, int resultCode, String errorMessage) {
        this.data = data;
        this.resultCode = resultCode;
        this.errorMessage = errorMessage;
    }

    public Map<String, Integer> getData() {
        return data;
    }
    public void setData(Map<String, Integer> data) {
        this.data = data;
    }
}
