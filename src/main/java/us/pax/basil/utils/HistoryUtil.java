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
 * 2022/01/27               rb
 * ============================================================================
 */

import us.pax.basil.mapper.UserMapper;
import us.pax.basil.security.CustomUserDetails;

public class HistoryUtil {

    //
    // Set session info data for table triggers
    //
    public static void setHistorySessionInfo(UserMapper userMapper, String programName, String changeReason) {
        setUserName(userMapper); 
        userMapper.setProgramName(programName);
        userMapper.setReasonForChange(changeReason);
    }

    public static void setUserName(UserMapper userMapper) {
        CustomUserDetails userDetails = AuthUtil.getUser();
        userMapper.setUserId(userDetails.getUsername());    
    }
}
