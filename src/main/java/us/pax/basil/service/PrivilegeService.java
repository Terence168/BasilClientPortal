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

import java.util.Map;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.entity.privilege.PasswordChange;
import us.pax.basil.entity.privilege.Role;
import us.pax.basil.entity.privilege.RoleType;
import us.pax.basil.entity.privilege.UserAddUpdate;
import us.pax.basil.utils.ColumnMapping;

public interface PrivilegeService extends IService<RoleType> {
    // role type
    SqlResultDTO AddRoleType(RoleType roleType);
    SqlResultDTO UpdateRoleType(RoleType roleType);
    QueryResultArrayDTO queryRoleType(EntityManager entityManager, Map<String, ColumnMapping> columnMapping, HttpServletRequest request);

    // role
    SqlResultDTO AddRole(Role role);
    SqlResultDTO UpdateRole(Role role);
    QueryResultArrayDTO QueryRole(HttpServletRequest request);
    QueryResultArrayDTO ViewQueryRole(HttpServletRequest request, String id);
    QueryResultArrayDTO queryAllRoles();

    // user
    QueryResultArrayDTO viewQueryUser(Integer id);

    SqlResultDTO addUser(User user);
    SqlResultDTO updateUser(User user);
    SqlResultDTO changePassword(PasswordChange passwordChange);
}
