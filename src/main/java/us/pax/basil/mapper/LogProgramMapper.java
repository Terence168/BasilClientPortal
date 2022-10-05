package us.pax.basil.mapper;
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

import java.util.HashMap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import us.pax.basil.entity.dblog.LogProgram;

public interface LogProgramMapper extends BaseMapper<LogProgram> {
    void addLogProgram(HashMap<String, Object> map);
    void updateLogProgram(HashMap<String, Object> map);

    void addLogProgramInfo(HashMap<String, Object> map);
}
