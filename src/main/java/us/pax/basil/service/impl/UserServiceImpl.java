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

import lombok.extern.log4j.Log4j2;
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

@Log4j2
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
	
	        user.setStatus(StatusConstant.DISABLED);
	
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
    		passwordMapper.setStatus(passToken, StatusConstant.ACTIVE);
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
        
        if (userStatus.equals(StatusConstant.DISABLED) == true) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently disabled.");
        } else if (userStatus.equals(StatusConstant.INACTIVE) == true) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently inactive.");
        }

        Collection<GrantedAuthority> authorities = details.getAuthorities();
        
        ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        
        map.put("name", details.getUsername());
        map.put("email", details.getEmailAddress());
        map.put("companyId", details.getCompanyId());
        
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
		
		try {
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
	
	             map.put("id", user.getId());
	             map.put("name", user.getName());
	             map.put("company", user.getCompanyId());
	             map.put("email", user.getEmail());
	             map.put("status", user.getStatus());
	             map.put("statusStr", user.getStatusStr());
	             
	             resultArray.add(map);
			}
	        return new QueryResultArrayDTO(resultArray, total, 0, "");
		} catch(Exception e) {
			return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
		}
	}

	@Override
	public QueryResultArrayDTO viewQuery(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResultArrayDTO queryPrivilegeUsers(Integer currentPage, 
												    Integer sizePerPage, String sortColumns, 
												    String userName, 
												    String email, 
												    String registerTime, 
												    String lastLogin,
												    Integer status) {

		try {
			Integer total = userMapper.getPrivilegeListCount(userName, email, registerTime, lastLogin, status);
	
			List<User> userList = userMapper.queryPrivilegeList((currentPage-1) * sizePerPage, 
					                                   sizePerPage, 
					                                   buildSortString(sortColumns), 
					                                   userName, 
					                                   email, 
					                                   registerTime, 
					                                   lastLogin,
					                                   status);
			
			ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
	
			for (User user : userList) {
	             Map<String, Object> map = new HashMap<>();
	
	             map.put("id", user.getId());
	             map.put("user", user.getName());
	             map.put("email", user.getEmail());
	             map.put("registerTime", user.getCreated());
	             map.put("lastLogin", user.getLastLoginDate());
	             map.put("status", user.getStatus());
	             map.put("statusStr", user.getStatusStr());
	             
	             resultArray.add(map);
			}
	        return new QueryResultArrayDTO(resultArray, total, 0, "");
		} catch(Exception e) {
			return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
		}
	}

    private String buildSortString(String sortColumns) {
        if (null == sortColumns) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        String[] sortCols = sortColumns.split(",");
        for (String col : sortCols) {
            String[] fields = col.split("\\.");
            if (fields.length > 2) {
                log.warn("Ignoring invalid sort field: {}", col);
                continue;
            }

            if (fields.length == 2) {
                if (fields[1].equalsIgnoreCase("asc") || fields[1].equalsIgnoreCase("desc")) {
                    col = col.replace(".", " ");
                } else {
                    log.warn("Ignoring invalid sort field: {}", col);
                    continue;
                }
            }

            switch (fields[0]) {
                case "email":
                    col = col.replace("email", "EMAIL");
                    break;
                case "user":
                    col = col.replace("user", "NAME");
                    break;
                case "registerTime":
                    col = col.replace("registerTime", "CREATED");
                    break;
                case "lastLogin":
                    col = col.replace("lastLogin", "LAST_LOGIN_DATE");
                    break;
                default:
                    log.warn("Ignoring invalid sort field: {}", col);
                    continue;
            }

            sb.append(col).append(",");
        }

        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1); // remove the comma at the end of the sort string
        }
        log.info("Sorting String: [{}]", sb.toString());
        return sb.toString();
    }
}