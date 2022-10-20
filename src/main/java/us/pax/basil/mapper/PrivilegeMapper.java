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

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import us.pax.basil.entity.User;
import us.pax.basil.entity.privilege.Role;
import us.pax.basil.entity.privilege.RoleType;
import us.pax.basil.entity.privilege.UserQuery;

public interface PrivilegeMapper extends BaseMapper<RoleType> {
    void addRoleType(Integer id, String name);
    int updateRoleType(Integer id,  String name);
    List<Map<String, Object>> queryRoleType();

    void addRole(Role role);
    void addRolePermission(Integer permission_id, Integer role_id);
    void updateRole(String role_name, Integer role_type_id, Integer role_id);
    void deleteRolePermissions(Integer id);
    List<Map<String, Object>> getRoleIdAndTitle();
    List<Map<String, Object>> getUserNameEmail(Integer role_id);
    List<Map<String, Object>> getTitleRtIdPrivId(Integer role_id);
    
    List<Map<String, Object>> getRoleTypeIdsNames();
    List<Map<String, Object>> getRoleIdsNames(Integer roleTypeId);
    
    User getUser(Integer id); 
    List<UserQuery>getUsers(); 
    void addUser(User user);
    void addUserRole(Integer user_id, Integer role_id);
    void deleteUserRoles(Integer user_id);
    List<Integer> getUserRoles(Integer id);
    void updateUser(User user);
    void changePassword(Integer id, String newPassword);
}
