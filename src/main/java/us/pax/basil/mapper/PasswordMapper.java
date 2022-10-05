package us.pax.basil.mapper;

import java.sql.Timestamp;


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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface PasswordMapper extends BaseMapper<Integer> {
	Timestamp getTokenExpiration(String token);
	void setStatus(String token, Integer status);
	void resetToken(String token, String resetToken);
    void saveTokenAndExpiration(String email, String token, Timestamp timeStamp);
    void savePassword(String token, String password);
}
