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
import java.util.LinkedHashMap;
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
	        
	        user.setPassToken(token);
	        user.setPassTokenExp(new Timestamp(System.currentTimeMillis() + PasswordConstant.EXPIRATION));

	        userMapper.addUser(user);

          	MimeMessage mimeMsg = mailSender.createMimeMessage();
            mimeMsg = EmailUtil.constructTokenEmail(mimeMsg, 
                                                     request, 
                                                     token, 
                                                     PasswordConstant.WELCOME_USER_HTML_FILE,
                                                     PasswordConstant.WELCOME_USER_SUBJECT,
                                                     PasswordConstant.FRONTEND_WELCOME_USER_URL,
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
    public SqlResultDTO activateUser(HttpServletRequest request, String password, String token) {
    	try {
    		User user = userMapper.getUserByToken(token);
    		if (user == null) {
    			return new SqlResultDTO(-200, "User account cannot be found for the specified token.");
    		}
    		passwordMapper.savePassword(token, passwordEncoder.encode(password));
    		passwordMapper.setStatus(token, StatusConstant.STATUS_ENABLED);
    		passwordMapper.resetToken(token, UUID.randomUUID().toString());
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
        
        Integer userStatus = userMapper.getUserStatus(details.getEmpOid());
        
        if (userStatus.equals(StatusConstant.USER_DISABLED) == true) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently disabled.");
        }

        Collection<GrantedAuthority> authorities = details.getAuthorities();
        
        ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        
        map.put("name", details.getUsername());
        map.put("email", details.getEmailAddress());
        map.put("xtOid", 999);
        //TODO: company name
        
        ArrayList<String> permissions = new ArrayList<>();
        
        for (GrantedAuthority g: authorities) {
            permissions.add(g.getAuthority());
        }
        map.put("permissions", permissions);

        jsonArray.add(map);

        return new QueryResultArrayDTO(jsonArray, permissions.size(), 0, "");
    }
}