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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import us.pax.basil.constant.ClientGroupConstant;
import us.pax.basil.constant.DropDownConstant;
import us.pax.basil.constant.PrivilegeConstant;
import us.pax.basil.constant.StatusConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.entity.customer.Company;
import us.pax.basil.entity.privilege.*;
import us.pax.basil.mapper.PrivilegeMapper;
import us.pax.basil.mapper.RoleEntityMapper;
import us.pax.basil.mapper.RoleTypeMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.PrivilegeService;
import us.pax.basil.utils.AuthUtil;
import us.pax.basil.utils.ColumnMapping;
import us.pax.basil.utils.HistoryUtil;
import us.pax.basil.utils.QueryAttributes;
import us.pax.basil.utils.QueryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class PrivilegeServiceImpl extends ServiceImpl<PrivilegeMapper, RoleType> implements PrivilegeService {

    private PrivilegeMapper privilegeMapper;
    private UserMapper userMapper;
    RoleTypeMapper roleTypeMapper;
    RoleEntityMapper roleMapper;

    //
    // Add() - Add customer information parameters passed in.
    //
    @Override
    public SqlResultDTO AddRoleType(RoleType roleType) {
        
        try {
            privilegeMapper.addRoleType(roleType.getId(), roleType.getName());

            return new SqlResultDTO(0, "");
        } catch(Exception e) {
            log.info("PrivilegeServiceImpl::AddRoleType(): ***exception: {}", e.getCause().getMessage());
            return new SqlResultDTO(-1, e.getCause().getMessage());
        }
    }

    //
    // Update() - Update customer information parameters passed in.
    //
    @Override
    public SqlResultDTO UpdateRoleType(RoleType roleType) {
        
        try {
            privilegeMapper.updateRoleType(roleType.getId(), roleType.getName());

            return new SqlResultDTO(0, "");
        } catch(Exception e) {
            log.info("PrivilegeServiceImpl::UpdateRoleType(): ***exception: {}", e.getCause().getMessage());
            return new SqlResultDTO(-1, e.getCause().getMessage());
        }
    }

    //
    // Query() - Query customer information and return data specified by the
    //           parameters passed.
    //
    @Override
    public QueryResultArrayDTO queryRoleType(EntityManager entityManager, Map<String, ColumnMapping> columnMapping, HttpServletRequest request) {
        ArrayList<Map<String, Object>> returnArray = new ArrayList<>();
        try {
            QueryAttributes queryAttributes = QueryUtils.executeSql(entityManager, 
                                                                PrivilegeConstant.SQL_QUERY_ROLE_TYPE, 
                                                                columnMapping, 
                                                                request.getParameterMap());

            int startIndex = queryAttributes.getStartIndex();

            int endIndex = queryAttributes.getEndIndex();

            int pageCount=0;
            int roleTypeCount=0;
            for (Object[] o: queryAttributes.getResult()) {
                ++roleTypeCount;
                if (pageCount >= startIndex && pageCount < endIndex) {
                    Map<String, Object> roleTypeMap = new HashMap<>();
                    
                    roleTypeMap.put(PrivilegeConstant.ID, o[0]);
                    roleTypeMap.put(PrivilegeConstant.ROLE_TYPE, o[1]);

                    returnArray.add(roleTypeMap);
                }
                ++pageCount;
            }
            return new QueryResultArrayDTO(returnArray, roleTypeCount, 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    //
    // AddRole()
    //
    @Override
    public SqlResultDTO AddRole(Role role) {
        try {
            CustomUserDetails userDetails = AuthUtil.getUser();
            
            if (userDetails == null) {
                log.info("PrivilegeServiceImpl::AddRole(): *** User not logged in");
                return new SqlResultDTO(-1, "User not logged in.");
            }

            role.setCreator(userDetails.getUsername());

            privilegeMapper.addRole(role);

            Integer role_id = role.getId();
            
            for(Integer permission_id: role.getPermissions()) {
                privilegeMapper.addRolePermission(permission_id, role_id);
            }
            return new SqlResultDTO(0, "");
        } catch(Exception e) {
            log.info("PrivilegeServiceImpl::AddRole(): ***exception: {}", e.getCause().getMessage());
            return new SqlResultDTO(-1, e.getCause().getMessage());
        }
    }

    //
    // updateRole()
    //
    @Override
    public SqlResultDTO UpdateRole(Role role) {
        try {
            privilegeMapper.updateRole(role.getRoleName(), role.getRoleTypeID(), role.getId());

            privilegeMapper.deleteRolePermissions(role.getId());
            for(Integer permission_id: role.getPermissions()) {
                privilegeMapper.addRolePermission(permission_id, role.getId());
            }
            
            return new SqlResultDTO(0, "");
        } catch(Exception e) {
            log.info("PrivilegeServiceImpl::DeleteRole(): ***exception: {}", e.getCause().getMessage());
            return new SqlResultDTO(-1, e.getCause().getMessage());
        }
    }

    @Override
    public QueryResultArrayDTO QueryRole(HttpServletRequest request) {
        ArrayList<Map<String, Object>> returnArray = new ArrayList<>();

        try {
            List<Map<String, Object>> rolesTitles = privilegeMapper.getRoleIdAndTitle();
            for (Map<String, Object> roleTitle: rolesTitles) {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put(PrivilegeConstant.ID, roleTitle.get("R_OID"));
                roleMap.put(PrivilegeConstant.NAME, roleTitle.get("NAME"));
    
                ArrayList<Map<String, Object>> userArray = new ArrayList<>();
    
                List<Map<String, Object>> namesEmails = privilegeMapper.getUserNameEmail((Integer)roleTitle.get(PrivilegeConstant.ROLE_ID));
                for (Map<String, Object> nameEmail: namesEmails) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put(PrivilegeConstant.ID, roleTitle.get(PrivilegeConstant.ROLE_ID));
                    userMap.put(PrivilegeConstant.USERNAME, nameEmail.get(PrivilegeConstant.USERNAME));
                    userMap.put(PrivilegeConstant.EMAIL, nameEmail.get(PrivilegeConstant.EMAIL));
                
                    userArray.add(userMap);
                }
                roleMap.put(PrivilegeConstant.USERS, userArray);
                returnArray.add(roleMap);
            }
            return new QueryResultArrayDTO(returnArray, returnArray.size(), 0, "");
        }catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    //
    // ViewQueryRole()
    //
    @Override
    public QueryResultArrayDTO ViewQueryRole(HttpServletRequest request, String id) {
        ArrayList<Map<String, Object>> returnArray = new ArrayList<>();

        try {
            List<Map<String, Object>> titlesRoleIdsPrivIds = privilegeMapper.getTitleRtIdPrivId(Integer.valueOf(id));
            boolean doOnce=false;
            Map<String, Object> trpMap = new HashMap<>();
            List<Integer> permissions =  new ArrayList<>();

            for(Map<String, Object> titleRoleIdPrivId: titlesRoleIdsPrivIds) {
                if (!doOnce) {
                    doOnce = true;
                    trpMap.put(PrivilegeConstant.ID, id);
                    trpMap.put(PrivilegeConstant.ROLE_NAME, titleRoleIdPrivId.get(PrivilegeConstant.TITLE_STRING_UPPERCASE));
                    trpMap.put(PrivilegeConstant.ROLE_TYPE_ID, titleRoleIdPrivId.get(PrivilegeConstant.RT_ID));
                }
                permissions.add((Integer)titleRoleIdPrivId.get(PrivilegeConstant.PRIVILEGE_ID));
            }
            trpMap.put(PrivilegeConstant.PERMISSIONS, permissions);
            returnArray.add(trpMap);
            return new QueryResultArrayDTO(returnArray, returnArray.size(), 0, "");
        }catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    @Override
    public QueryResultArrayDTO queryAllRoles() {
        try {
            ArrayList<Map<String, Object>> allRoleList = new ArrayList<>();

            QueryWrapper<RoleTypeEntity> roleTypeWrapper = new QueryWrapper<>();
            roleTypeWrapper.select("RT_ID", "NAME");
            List<RoleTypeEntity> roleTypeList = roleTypeMapper.selectList(roleTypeWrapper);
            for (RoleTypeEntity roleType : roleTypeList) {
                QueryWrapper<RoleEntity> roleWrapper = new QueryWrapper<>();
                roleWrapper.select("ROLE_ID", "TITLE").eq("RT_ID", roleType.getRtId());
                List<RoleEntity> roleList = roleMapper.selectList(roleWrapper);

                List<Map<String, Object>> roleInfo = new ArrayList<>();
                for (RoleEntity role : roleList) {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put(PrivilegeConstant.ID, role.getRoleId());
                    roleMap.put(PrivilegeConstant.ROLE_NAME, role.getTitle());
                    roleInfo.add(roleMap);
                }

                Map<String, Object> roleTypeMap = new HashMap<>();
                roleTypeMap.put(PrivilegeConstant.ROLE_TYPE_NAME, roleType.getName());
                roleTypeMap.put(PrivilegeConstant.ROLES, roleInfo);

                allRoleList.add(roleTypeMap);
            }

            return new QueryResultArrayDTO(allRoleList, allRoleList.size(), 0, "");
        } catch (Exception e) {
            log.error(e);
            log.error("Failed to query role list!");
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    //
    // addUser()
    //
    @Override
    public SqlResultDTO addUser(User user) {
        try { 
            CustomUserDetails userDetails = AuthUtil.getUser();
            assert userDetails != null;
            user.setCreator(userDetails.getUsername());
            userMapper.addUser(user);
            
            for (Integer i: user.getRoles()) {
                privilegeMapper.addUserRole(user.getId(), i);
            }
            return new SqlResultDTO(0, "");
        } catch(Exception e) {
            log.info("PrivilegeServiceImpl::addUser(): ***exception: {}", e.getCause().getMessage());
            return new SqlResultDTO(-1, e.getCause().getMessage());
        }
    }

    //
    // viewQueryUser()
    //
    @Override
    public QueryResultArrayDTO viewQueryUser(Integer id) {
        ArrayList<Map<String, Object>> returnArray = new ArrayList<>();
        try {
            User user = userMapper.getUserById(id);
            Map<String, Object> m = new HashMap<>();
            m.put("name", user.getName());
            m.put("email", user.getEmail());
            m.put("registerTime", user.getCreated());
            m.put("lastLogin", user.getLastLoginDate());
            m.put("status", user.getStatus());
            m.put("statusStr", user.getStatusStr());
            
            Company company = userMapper.getCompanyInfo(user.getCompanyId());
            Map<String, Object> mm = new HashMap<>();

            mm.put(DropDownConstant.DROPDOWN_VALUE, company.getId());
            mm.put(DropDownConstant.DROPDOWN_LABEL, company.getOrganization());
            mm.put(ClientGroupConstant.ID, company.getClientGroupId()); 
            mm.put(ClientGroupConstant.GROUP, company.getClientGroup());
            m.put("customerName", mm);

            List<Integer> roles =  privilegeMapper.getUserRoles(id);

            ArrayList<Integer> roleList = new ArrayList<>(roles);
            m.put(PrivilegeConstant.ROLES, roleList);
            
            returnArray.add(m);

            return new QueryResultArrayDTO(returnArray, returnArray.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    //
    // updateUser()
    //
    @Override
    public SqlResultDTO updateUser(User user) {
        try { 
            HistoryUtil.setHistorySessionInfo(userMapper, "PrivilegeMapper.xml:updateUser","User Update");

            if (user.getStatus() == null)
            	user.setStatus(StatusConstant.DISABLED);

            userMapper.updateUser(user);

            privilegeMapper.deleteUserRoles(user.getId());
            
            for (Integer i: user.getRoles()) {
                privilegeMapper.addUserRole(user.getId(), i);
            }

            return new SqlResultDTO(0, "");
        } catch(Exception e) {
            log.info("PrivilegeServiceImpl::addUser(): ***exception: {}", e.getCause().getMessage());
            return new SqlResultDTO(-1, e.getCause().getMessage());
        }
    }

    //
    // changePassword()
    //
    @Override
    public SqlResultDTO changePassword(PasswordChange passwordChange) {
        CustomUserDetails userDetails = AuthUtil.getUser();

        try {
            PasswordEncoder p = new BCryptPasswordEncoder();

            assert userDetails != null;
            if (!p.matches(passwordChange.getCurrentPassword(), userDetails.getPassword())) {
                return new SqlResultDTO(-1, "Current password entered does not match user's password.");
            }

            privilegeMapper.changePassword(userDetails.getUserId(), p.encode(passwordChange.getNewPassword()));

            return new SqlResultDTO(0, "");
        } catch (Exception e) {
            log.info("PrivilegeServiceImpl::changePassword(): ***exception: {}", e.getCause().getMessage());
            return new SqlResultDTO(-1, e.getCause().getMessage());
        }
    }
}
