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
 * 2020/04/24               yinyy
 * ============================================================================
 */


import com.baomidou.mybatisplus.extension.service.IService;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

public interface UserService extends IService<User> {
	SqlResultDTO addUser(HttpServletRequest request, User user);
	SqlResultDTO activateUser(HttpServletRequest request, String email, String token);

    boolean checkPassword(String password, String passwordEncoder);

    QueryResultArrayDTO getUserDetail(Authentication authentication);
}
