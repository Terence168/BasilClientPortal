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
 * 2020/04/24               yinyy
 * ============================================================================
 */

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import us.pax.basil.dto.input.UserCreateDTO;
import us.pax.basil.entity.User;

public interface UserMapper extends BaseMapper<User> {

    Integer getUserStatus(Integer id);
	User getUserByEmail(String email);
	User getUserByToken(String token);
    User getUserById(Integer id);
    
    void updateUser(User user);

	String getCompanyName(Integer id);
	void addUser(User user);
    void setLastLogin(Integer id);
    void setUserId(String userId);
    void setProgramName(String programName);
    void setReasonForChange(String reasonForChange);
    void createUser(UserCreateDTO user);
    
    List<User> queryList(int offset,
                         int count,
                         String sortColumns,
                         String name,
                         Integer company,
                         String email,
                         Integer status);

    List<User> queryPrivilegeList(int offset,
                         int count,
                         String sortColumns,
                         String name,
                         String email,
                         String registerTime,
                         String lastLogin,
                         Integer status);

    Integer getPrivilegeListCount(String name, 
    		                      String email, 
    		                      String registerTime, 
    		                      String lastLogin,
                         		  Integer status);

    Integer getListCount(String name,
                         Integer company,
                         Integer status,
                         String email);
}
