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
 * 2020/04/24               yinyy
 * ============================================================================
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import us.pax.basil.constant.PasswordConstant;
import us.pax.basil.constant.StatusConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.mapper.PasswordMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.property.FrontEndProperties;
import us.pax.basil.property.MailProperties;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.UserService;
import us.pax.basil.utils.AuthUtil;
import us.pax.basil.utils.EmailUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordMapper passwordMapper;

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private FrontEndProperties frontEndProperties;

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SqlResultDTO addUser(HttpServletRequest request, User user) {
    	try {
	    	User userAccount = userMapper.getUserByEmail(user.getEmail());
	    	
	    	if (userAccount != null) {
	    		return new SqlResultDTO(-300, "User account already exists");
	    	}
	
	    	//
	    	// Default password for new users
	    	//
	        //user.setPassword(passwordEncoder.encode(PasswordConstant.NEW_USER_PASSWORD));
	        user.setPassword(passwordEncoder.encode("Pax4Future!@"));
	
	        user.setStatus(StatusConstant.USER_INACTIVE);
	
	        CustomUserDetails userDetails = AuthUtil.getUser();
	        
	        if (userDetails == null)
	        	user.setCreator("test");
	        else
	        	user.setCreator(userDetails.getUsername());
	

	        final String token = UUID.randomUUID().toString();
	        
	        user.setToken(token);
	        user.setTokenExp(new Timestamp(System.currentTimeMillis() + PasswordConstant.EXPIRATION));

	        userMapper.addUser(user);

          	MimeMessage mimeMsg = mailSender.createMimeMessage();
            mimeMsg = EmailUtil.constructTokenEmail(mimeMsg, 
                                                    request, 
                                                    token, 
                                                    PasswordConstant.WELCOME_USER_HTML_FILE,
                                                    PasswordConstant.WELCOME_USER_SUBJECT,
                                                    frontEndProperties.getActivate(),
                                                    PasswordConstant.WELCOME_USER_LINK_TITLE,
                                                    user,
                                                    mailProperties.getUsername());
    		mailSender.send(mimeMsg);
    	} catch(Exception e) {
    		return new SqlResultDTO(0, e.getMessage());
    	}
        return new SqlResultDTO(0, "");
    }

    @Override
    public SqlResultDTO activateUser(HttpServletRequest request, User user) {
    	try {
    		String passToken = user.getToken();

    		User userAcctInfo = userMapper.getUserByToken(passToken);
    		if (userAcctInfo == null) {
    			return new SqlResultDTO(-200, "User account cannot be found for the specified token.");
    		}
    		passwordMapper.savePassword(passToken, passwordEncoder.encode(user.getPassword()));
    		passwordMapper.setStatus(passToken, StatusConstant.STATUS_ENABLED);
    		passwordMapper.resetToken(passToken, UUID.randomUUID().toString());
    	} catch (Exception e) {
    		return new SqlResultDTO(-1, e.getMessage());
    	}
        return new SqlResultDTO(0, "");
    }

    @Override
    public boolean checkPassword(String password, String passwordEnc) {
        return passwordEncoder.matches(password, passwordEnc);
    }

    @Override
    public QueryResultArrayDTO getUserDetail(Authentication authentication) {
        CustomUserDetails details = (CustomUserDetails)authentication.getPrincipal();
        
        Integer userStatus = userMapper.getUserStatus(details.getUserId());
        
        if (userStatus.equals(StatusConstant.USER_DISABLED) == true) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently disabled.");
        } else if (userStatus.equals(StatusConstant.STATUS_NOT_ENABLE) == true) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently not enabled.");
        } else if (userStatus.equals(StatusConstant.STATUS_DISABLED) == true ){
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently disabled.");
        } else if (userStatus.equals(StatusConstant.STATUS_DELETED) == true) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently deleted.");
        } else if (userStatus.equals(StatusConstant.USER_INACTIVE) == true) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently inactive.");
        }

        Collection<GrantedAuthority> authorities = details.getAuthorities();
        
        ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        
        map.put("name", details.getUsername());
        map.put("email", details.getEmailAddress());
        map.put("companyName", details.getCompanyName());
        
        ArrayList<String> permissions = new ArrayList<>();
        
        for (GrantedAuthority g: authorities) {
            permissions.add(g.getAuthority());
        }
        map.put("permissions", permissions);

        jsonArray.add(map);

        return new QueryResultArrayDTO(jsonArray, permissions.size(), 0, "");
    }

	@Override
	public QueryResultArrayDTO queryList(Integer currentPage,
			                              Integer sizePerPage,
			                              String sortColumns,
			                              String name, 
			                              Integer company, 
			                              String email, 
			                              Integer status) {
		
		Integer total = userMapper.getListCount(name, company, status, email);

		List<User> userList = userMapper.queryList((currentPage-1) * sizePerPage, 
				                                   sizePerPage, 
				                                   sortColumns, 
				                                   name, 
				                                   company, 
				                                   email, 
				                                   status);
		
		ArrayList<Map<String, Object>> resultArray = new ArrayList<>();

		for (User user : userList) {
             Map<String, Object> map = new HashMap<>();

             map.put("uOid", user.getUOid());
             map.put("name", user.getName());
             map.put("company", user.getCompanyId());
             map.put("email", user.getEmail());
             map.put("status", user.getStatus());
             
             resultArray.add(map);
		}
        return new QueryResultArrayDTO(resultArray, total, 0, "");

	}

	@Override
	public QueryResultArrayDTO viewQuery(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}