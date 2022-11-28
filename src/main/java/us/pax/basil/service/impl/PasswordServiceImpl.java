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
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.mapper.PasswordMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.property.FrontEndProperties;
import us.pax.basil.property.MailProperties;
import us.pax.basil.service.PasswordService;
import us.pax.basil.utils.EmailUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class PasswordServiceImpl extends ServiceImpl<PasswordMapper, Integer> implements PasswordService {
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private FrontEndProperties frontEndProperties;
    
    @Autowired
    private MailProperties mailProperties;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private PasswordMapper passwordMapper;
    private UserMapper userMapper;

    //
    // forgotPassword() -  User has forgotten their password, so they will have to reset their password.  Send them
    //                     an email with a link to the frontend to initiate a password reset.
    @Override
    public SqlResultDTO forgotPassword(HttpServletRequest request, String email) {
    	try {
	        //
	        // Get user account information
	        //
	        User user = userMapper.getUserByEmail(email);
	
	        if (user == null) {
	            return new SqlResultDTO(-200, "User account cannot be found with the email: " + email);
	        }
	
	        //
	        // Generate token, token expiration timestamp and save it to the database
	        //
	        final String token = UUID.randomUUID().toString();
	        passwordMapper.saveTokenAndExpiration(email, token, new Timestamp(System.currentTimeMillis() + PasswordConstant.EXPIRATION));
	
	        MimeMessage mimeMsg = mailSender.createMimeMessage();
	        mimeMsg = EmailUtil.constructTokenEmail(mimeMsg, 
	                                                     request, 
	                                                     token, 
	                                                     PasswordConstant.FORGOT_PASSWORD_HTML_FILE,
	                                                     PasswordConstant.PASSWORD_CHANGE_EMAIL_SUBJECT,
	                                                     frontEndProperties.getChange(),
	                                                     PasswordConstant.CHANGE_PASSWORD_LINK_TITLE,
	                                                     user, 
	                                                     mailProperties.getUsername());
	        mailSender.send(mimeMsg);
    	} catch(Exception e) {
            log.error("Exception processing forgotten password: {}", e.getMessage());
    		return new SqlResultDTO(-1, e.getMessage());
    	}

        return new SqlResultDTO(0, "");
    }

    @Override
    public SqlResultDTO savePassword() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SqlResultDTO resetPassword(HttpServletRequest request, String password, String token) {
        Timestamp expirationTs = passwordMapper.getTokenExpiration(token);

        if (expirationTs == null) {
            return new SqlResultDTO(-200, "User account cannot be found with the token.");
        }

        Timestamp currentTs = new Timestamp(System.currentTimeMillis());
        if (currentTs.after(expirationTs)) {
            return new SqlResultDTO(-100, "Time has expired to reset your password.");
        }
        
        passwordMapper.savePassword(token, passwordEncoder.encode(password));

        return new SqlResultDTO(0, "");
    }

	@Override
	public SqlResultDTO tokenValid(String token) {
		User user = userMapper.getUserByToken(token);
        if (user != null) {
            return new SqlResultDTO(0, "");
        } else
            return new SqlResultDTO(-1, "Token does not exist.");
	}
}
