package us.pax.basil.service;
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

import com.baomidou.mybatisplus.extension.service.IService;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.LabelValuePair;

public interface SupportAttributeService extends IService<LabelValuePair> {
    QueryResultArrayDTO getDropDown(Integer id);
    QueryResultArrayDTO getCompanyInfo();
}
