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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.xalan.lib.sql.ObjectArray;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;
import us.pax.basil.constant.ClientGroupConstant;
import us.pax.basil.constant.DropDownConstant;
import us.pax.basil.constant.PrivilegeConstant;
import us.pax.basil.constant.StatusConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.entity.customer.Address;
import us.pax.basil.entity.customer.Company;
import us.pax.basil.entity.customer.Customer;
import us.pax.basil.entity.privilege.*;
import us.pax.basil.mapper.PrivilegeMapper;
import us.pax.basil.mapper.RoleEntityMapper;
import us.pax.basil.mapper.RoleTypeMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.AddressService;
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
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class PrivilegeServiceImpl extends ServiceImpl<PrivilegeMapper, RoleType> implements PrivilegeService {

    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired(required=false)
    private PrivilegeMapper privilegeMapper;
    @Autowired(required=false)
    private UserMapper userMapper;
    @Autowired(required=false)
    RoleTypeMapper roleTypeMapper;
    @Autowired(required=false)
    RoleEntityMapper roleMapper;
    @Autowired
    private AddressService addressService;

    private ObjectMapper objectMapper = new ObjectMapper();
    //
    // Add() - Add customer information parameters passed in.
    //
    @Override
    public SqlResultDTO AddRoleType(RoleType roleType) {

        try {
            CustomUserDetails userDetails = AuthUtil.getUser();
            assert userDetails != null;
            privilegeMapper.addRoleType(roleType.getName(), userDetails.getUsername());

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

                List<Map<String, Object>> namesEmails = privilegeMapper.getUserNameEmail((Integer)roleTitle.get("R_OID"));
                for (Map<String, Object> nameEmail: namesEmails) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put(PrivilegeConstant.ID, nameEmail.get("U_OID"));
                    userMap.put(PrivilegeConstant.USERNAME, nameEmail.get("NAME"));
                    userMap.put(PrivilegeConstant.EMAIL, nameEmail.get("EMAIL"));

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
    public QueryResultArrayDTO ViewQueryRole(HttpServletRequest request, Integer id) {
        ArrayList<Map<String, Object>> returnArray = new ArrayList<>();

        try {
            List<Map<String, Object>> titlesRoleIdsPrivIds = privilegeMapper.getTitleRtIdPrivId(id);
            boolean doOnce=false;
            Map<String, Object> trpMap = new HashMap<>();
            List<Integer> permissions =  new ArrayList<>();

            for(Map<String, Object> titleRoleIdPrivId: titlesRoleIdsPrivIds) {
                if (!doOnce) {
                    doOnce = true;
                    trpMap.put(PrivilegeConstant.ID, id);
                    trpMap.put(PrivilegeConstant.ROLE_NAME, titleRoleIdPrivId.get("NAME"));
                    trpMap.put(PrivilegeConstant.ROLE_TYPE_ID, titleRoleIdPrivId.get("RT_OID"));
                }
                permissions.add((Integer)titleRoleIdPrivId.get("P_OID"));
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
            roleTypeWrapper.select("RT_OID", "NAME");
            List<RoleTypeEntity> roleTypeList = roleTypeMapper.selectList(roleTypeWrapper);
            for (RoleTypeEntity roleType : roleTypeList) {
                QueryWrapper<RoleEntity> roleWrapper = new QueryWrapper<>();
                roleWrapper.select("R_OID", "NAME").eq("RT_OID", roleType.getRtOid());
                List<RoleEntity> roleList = roleMapper.selectList(roleWrapper);

                List<Map<String, Object>> roleInfo = new ArrayList<>();
                for (RoleEntity role : roleList) {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put(PrivilegeConstant.ID, role.getROid());
                    roleMap.put(PrivilegeConstant.ROLE_NAME, role.getName());
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
            m.put("standardUser", user.getStandardUser());

            if(user.getStandardUser() == 1) {
                Company company = userMapper.getCompanyInfo(user.getCompanyId());
                if (company == null) {
                    return new QueryResultArrayDTO(null, 0, -1, "Company ID: " + user.getCompanyId() + " does not exist.");
                }

                Map<String, Object> mm = new HashMap<>();

                mm.put(DropDownConstant.DROPDOWN_VALUE, company.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, company.getOrganization());
                mm.put(ClientGroupConstant.ID, company.getClientGroupId());
                mm.put(ClientGroupConstant.GROUP, company.getClientGroup());
                m.put("customerName", mm);
            }

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
    	CustomUserDetails currentUser = AuthUtil.getUser();

        try {
            HistoryUtil.setHistorySessionInfo(userMapper, "PrivilegeMapper.xml:updateUser","User Update");

            if (user.getStatus() == null)
            	user.setStatus(StatusConstant.DISABLED);

            if (user.getStandardUser() == null)
            	user.setStandardUser(0);

            if(user.getId() == null){
                assert currentUser != null;
                user.setId(currentUser.getUserId());
            }
            Integer companyId = user.getCompanyId();
            
            if (companyId == null) {
                companyId = 0;
            }
            
            assert currentUser != null;
            if(!currentUser.canViewOrEditOtherCustomersRecords(companyId)){
                return new SqlResultDTO(-1, "Don't have access to update the user.");
            }

            userMapper.updateUser(user);

            privilegeMapper.deleteUserRoles(user.getId());

            for (Integer i: user.getRoles()) {
                privilegeMapper.addUserRole(user.getId(), i);
            }

            if (currentUser.getUsername().compareTo(user.getName())!=0) {
            	AuthUtil.logoutUser(sessionRegistry, user.getName());
            } else {
            	AuthUtil.setUserCompanyId(user.getCompanyId());
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

    // generate all privileges list
    @Override
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public QueryResultArrayDTO queryAllPrivileges(HttpServletRequest request) {
        ArrayList<Map<String, Object>> returnArray = new ArrayList<>();
        try {
            List<Map<String, Object>> privileges = privilegeMapper.queryAllPrivileges();
            Privilege privilege_root = new Privilege(0, "All Permissions", null);
            Map<Integer, Privilege> privilegeMap = new HashMap<>();
            privilegeMap.put(0, privilege_root);
            for (Map<String, Object> privilege : privileges) {
                int id = (int) privilege.get("P_OID");
                String name = (String) privilege.get("NAME");
                Privilege privilegeCurr = new Privilege(id, name, null);
                privilegeMap.put(id, privilegeCurr);
            }

            for (Map<String, Object> privilege : privileges) {
                int id = (int) privilege.get("P_OID");
                int parentId = (int) privilege.get("PARENT_OID");
                Privilege privilege_parent = privilegeMap.get(parentId);
                Privilege privilege_child = privilegeMap.get(id);
                assert privilege_parent != null;
                privilege_parent.addChild(privilege_child);
            }

            returnArray.add(privilege_root.toMap());
            return new QueryResultArrayDTO(returnArray, privileges.size(), 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    // generate user's privileges list
    @Override
    public QueryResultArrayDTO queryUserPrivileges(HttpServletRequest request) {
        ArrayList<Map<String, Object>> returnArray = new ArrayList<>();
        try {
            List<Map<String, Object>> privileges = privilegeMapper.queryAllPrivileges();
            CustomUserDetails user = AuthUtil.getUser();
            Privilege privilege_root = new Privilege(0, "All Permissions", null);
            int cnt = 1;
            Map<Integer, Privilege> privilegeMap = new HashMap<>();
            privilegeMap.put(0, privilege_root);
            for (Map<String, Object> privilege : privileges) {
                int id = (int) privilege.get("P_OID");
                String name = (String) privilege.get("NAME");
                Privilege privilegeCurr = new Privilege(id, name, null);
                privilegeMap.put(id, privilegeCurr);
            }

            for (Map<String, Object> privilege : privileges) {
                int id = (int) privilege.get("P_OID");
                int parentId = (int) privilege.get("PARENT_OID");
                Privilege privilege_parent = privilegeMap.get(parentId);
                Privilege privilege_child = privilegeMap.get(id);
                int access_control = (int) privilege.get("ACCESS_CONTROL");
                assert privilege_parent != null;
                assert user != null;
                if ((access_control & 1) != 0 && user.isClientUser()) {
                    privilege_parent.addChild(privilege_child);
                    ++cnt;
                } else if ((access_control & 2) != 0 && !user.isClientUser()) {
                    privilege_parent.addChild(privilege_child);
                    ++cnt;
                }
            }
            returnArray.add(privilege_root.toMap());
            return new QueryResultArrayDTO(returnArray, cnt, 0, "");
        } catch (Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
    }

    @Override
    public QueryResultDTO updateCustomer(Customer customer) {
        try{
            privilegeMapper.updateCustomer(customer);
            return new QueryResultDTO(null, 0,  "");
        }catch (Exception e){
            return new QueryResultDTO(null, -1,  e.getMessage());
        }
    }

    @Override
    public QueryResultDTO getCustomer() {
        try{
            CustomUserDetails userDetails = AuthUtil.getUser();
            Integer id = userDetails.getCompanyId();
            Customer customer = privilegeMapper.getCustomer(id);
            Map<String, Object> map = null;
            if(customer != null){
                map = objectMapper.convertValue(customer, Map.class);
            }
            Address defaultAddress = addressService.findDefaultAddress(id);
            if(defaultAddress != null){
                Map<String, Object> addressMap = objectMapper.convertValue(defaultAddress, Map.class);
                map.put("address", addressMap);
            }
            else{
                map.put("address", null);
            }
            return new QueryResultDTO(map, 0,  "");
        }catch (Exception e){
            return new QueryResultDTO(null, -1,  e.getMessage());
        }
    }


}
