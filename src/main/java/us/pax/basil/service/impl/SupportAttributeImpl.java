package us.pax.basil.service.impl;
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
 * 2021/07/06               rb
 * ============================================================================
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.entity.LabelValuePair;
import us.pax.basil.mapper.SupportAttributeMapper;
import us.pax.basil.service.SupportAttributeService;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class SupportAttributeImpl extends ServiceImpl<SupportAttributeMapper, LabelValuePair> implements SupportAttributeService {
    @Autowired
    private SupportAttributeMapper supportAttributeMapper;

    @Override
    public QueryResultArrayDTO getDropDown(Integer oid) {
        try {
            ArrayList<Map<String, Object>> dropDown = supportAttributeMapper.getValue(oid);
            return new QueryResultArrayDTO(dropDown, dropDown.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

	@Override
	public QueryResultArrayDTO getCompanyInfo() {
        try {
            ArrayList<Map<String, Object>> dropDown = supportAttributeMapper.getCompanyInfo();
            return new QueryResultArrayDTO(dropDown, dropDown.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}
}
