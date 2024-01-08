package us.pax.basil.service.impl;
/*
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
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import us.pax.basil.constant.ClientGroupConstant;
import us.pax.basil.constant.DropDownConstant;
import us.pax.basil.constant.PasswordConstant;
import us.pax.basil.constant.StatusConstant;
import us.pax.basil.dto.output.QueryResultArrayDTO;
import us.pax.basil.dto.output.QueryResultDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.entity.customer.Company;
import us.pax.basil.mapper.PasswordMapper;
import us.pax.basil.mapper.PrivilegeMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.property.FrontEndProperties;
import us.pax.basil.security.CustomUserDetails;
import us.pax.basil.service.UserService;
import us.pax.basil.service.aws.ses.EmailService;
import us.pax.basil.utils.AuthUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired(required=false)
    private UserMapper userMapper;

    @Autowired(required=false)
    private PasswordMapper passwordMapper;

    @Autowired(required=false)
    private PrivilegeMapper privilegeMapper;
    
    @Autowired(required=false)
    private FrontEndProperties frontEndProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private EmailService emailService;

    @Override
    public CompletableFuture<SqlResultDTO> addUserAsync(HttpServletRequest request, User user) {
        User userAccount = userMapper.getUserByEmail(user.getEmail());

        if (userAccount != null) {
            return CompletableFuture.completedFuture(new SqlResultDTO(-300, "User account already exists"));
        }

        user.setPassword(passwordEncoder.encode("DefaultEncodedPassword"));
        CustomUserDetails userDetails = AuthUtil.getUser();
        user.setCreator(userDetails != null ? userDetails.getUsername() : "test");
        final String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setTokenExp(new Timestamp(System.currentTimeMillis() + PasswordConstant.EXPIRATION));
        if (user.getCompanyId() == null) {
            user.setCompanyId(178);
        }

        userMapper.addUser(user);
        if (user.getRoles() != null) {
            for (Integer roleId : user.getRoles()) {
                privilegeMapper.addUserRole(user.getId(), roleId);
            }
        }

        String activateUrl = frontEndProperties.getActivate() + token;
        String subject = PasswordConstant.WELCOME_USER_SUBJECT;
        String message = "Welcome To The Basil Client Portal!<br><br>" +
                "Hi " + user.getName() + ",<br><br>" +
                "A user account has been created for you on the Basil Client Portal. Please click on the link below to activate your account and set your password. This link remains active for 24hrs.<br><br>" +
                "<a href=\"" + activateUrl + "\">Activate Account</a><br><br>" +
                "Best regards,<br>PAX Support Team";

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("title", subject);
        templateData.put("message", message);
        templateData.put("subject", subject);

        return emailService.sendTemplatedEmail(subject, "simpleMessage", templateData, user.getEmail())
                .thenApply(sesResponses -> new SqlResultDTO(0, "User added and email sent successfully."))
                .exceptionally(e -> {
                    log.error("Error sending welcome email: {}", e.getMessage(), e);
                    return new SqlResultDTO(-1, "User added but failed to send welcome email.");
                });
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
            log.error("Exception activating user: {}", e.getMessage());
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
        
        User user= userMapper.getUserById(details.getUserId());
       
        if (user.getStatus().equals(StatusConstant.DISABLED)) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently disabled.");
        } else if (user.getStatus().equals(StatusConstant.INACTIVE)) {
            return new QueryResultArrayDTO(null, 0, -55, "User account is currently inactive.");
        }

        Collection<GrantedAuthority> authorities = details.getAuthorities();
        
        ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("companyId", user.getCompanyId());
        map.put("companyName", userMapper.getCompanyInfo(user.getCompanyId()).getOrganization());
        map.put("clientUser", user.getStandardUser());
        
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
            CustomUserDetails currUser = AuthUtil.getUser();
            Integer companyId = null;

            if (currUser != null)
                if(currUser.getStandardUser() == 1){
                    if(company != null){
                        return new QueryResultArrayDTO(null, 0, -1, "Don't have access it.");
                    }
                    companyId = currUser.getCompanyId();
                }
                else {
                    companyId = company;
                }

			Integer total = userMapper.getListCount(name, companyId, status, email);
	
			List<User> userList = userMapper.queryList((currentPage-1) * sizePerPage, 
					                                   sizePerPage, 
					                                   sortColumns, 
					                                   name, 
					                                   companyId,
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
            CustomUserDetails currUser = AuthUtil.getUser();
            Integer companyId = null;

            if (currUser != null)
                //is client user
                if(currUser.getStandardUser() == 1){
                    companyId = currUser.getCompanyId();
                }

//			Integer total = userMapper.getPrivilegeListCount(userName, email, registerTime, lastLogin, status, companyId);
	
			List<User> userList = userMapper.queryPrivilegeList((currentPage-1) * sizePerPage, 
					                                   sizePerPage, 
					                                   buildSortString(sortColumns), 
					                                   userName, 
					                                   email, 
					                                   registerTime, 
					                                   lastLogin,
					                                   status, companyId);
            Integer total = userList.size();
			
			ArrayList<Map<String, Object>> resultArray = new ArrayList<>();
	
			for (User user : userList) {
	             Map<String, Object> map = new HashMap<>();
	
	             map.put("id", user.getId());
	             map.put("name", user.getName());
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

	@Override
	public QueryResultArrayDTO queryCompany(String name) {
        try {
        	
        	List<Company> companyList = userMapper.queryCompanyList(name);

            ArrayList<Map<String, Object>> jsonArray = new ArrayList<>();

            for (Company company: companyList) {
                Map<String, Object> mm = new LinkedHashMap<String, Object>();

                mm.put(DropDownConstant.DROPDOWN_VALUE, company.getId());
                mm.put(DropDownConstant.DROPDOWN_LABEL, company.getOrganization());
                mm.put(ClientGroupConstant.ID, company.getClientGroupId()); 
                mm.put(ClientGroupConstant.GROUP, company.getClientGroup());

               jsonArray.add(mm);
            }
            
            return new QueryResultArrayDTO(jsonArray, jsonArray.size(), 0, "");
        }catch(Exception e) {
            return new QueryResultArrayDTO(null, 0, -1, e.getMessage());
        }
	}

    @Override
    public QueryResultDTO updateUserInfo(User user, Integer userId) {
        try {

            User userAccount = userMapper.getUserByEmail(user.getEmail());

            if (userAccount != null) {
                return new QueryResultDTO(null, -300, "Email already exists");
            }

            userMapper.updateUserInfo(user, userId);

            return new QueryResultDTO(null, 0,  "");
        }catch(Exception e) {
            return new QueryResultDTO(null, 0, e.getMessage());
        }
    }

    @Override
    public QueryResultDTO updateUserEmail(String email) {
        try {

            User userAccount = userMapper.getUserByEmail(email);

            if (userAccount != null) {
                return new QueryResultDTO(null, -300, "Email already exists");
            }
            CustomUserDetails customUserDetails = AuthUtil.getUser();
            Integer userId = customUserDetails.getUserId();
            User user = new User();
            user.setEmail(email);
            userMapper.updateUserInfo(user, userId);

            return new QueryResultDTO(null, 0,  "");
        }catch(Exception e) {
            return new QueryResultDTO(null, 0, e.getMessage());
        }
    }
}