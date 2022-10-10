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
 * 2020/05/06               yinyy
 * ============================================================================
 */


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import us.pax.basil.entity.User;
import us.pax.basil.mapper.EmployeeMapper;
import us.pax.basil.mapper.PermissionMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.security.CustomUserDetails;

/**
 * @author yinyy
 * 2020-05-06 16:36
 **/
@Service("userDetailsService")
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String loginName) {
        // Get user information from database
    	User user = userMapper.getUserByEmail(loginName);

        if (user == null) {
            log.error("User does not exist: {}", loginName);
            throw new AuthenticationCredentialsNotFoundException("Incorrect Credential: " + loginName);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        /*
        List <String> privilegeList = permissionMapper.getPrivilegeListByEmpOid(employee.getEmpOid());
        if (!CollectionUtils.isEmpty(privilegeList)) {
            privilegeList.forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege)));
        }
        */

        return new CustomUserDetails(user.getName(), user.getPassword(), authorities)
                .setUserId(user.getUOid())
                .setEmailAddress(user.getEmail())
                .setCompanyName(userMapper.getCompanyName(user.getCompanyId()));
    }
}
