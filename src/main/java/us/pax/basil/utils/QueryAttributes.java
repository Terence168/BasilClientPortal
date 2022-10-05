package us.pax.basil.utils;

import java.util.List;
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
 * Date                     Author                    Action
 * 2021/05/10               rb
 * ============================================================================
 */

@Data
public class QueryAttributes {
    private String perPage;
    private String page;
    private String sql;
    private String whereString;
    private String orderString;
    private int startIndex;
    private int endIndex;
    private List <Object[]> result;
}
