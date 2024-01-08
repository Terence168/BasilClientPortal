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
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import us.pax.basil.constant.PasswordConstant;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.mapper.PasswordMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.property.FrontEndProperties;
import us.pax.basil.service.PasswordService;
import us.pax.basil.service.aws.ses.EmailService;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@AllArgsConstructor
public class PasswordServiceImpl extends ServiceImpl<PasswordMapper, Integer> implements PasswordService {
    
    @Autowired
    private FrontEndProperties frontEndProperties;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    private EmailService emailService;

    private PasswordMapper passwordMapper;
    private UserMapper userMapper;

    @Override
    public CompletableFuture<SqlResultDTO> forgotPasswordAsync(HttpServletRequest request, String email) {
        try {
            User user = userMapper.getUserByEmail(email);
            if (user == null) {
                return CompletableFuture.completedFuture(
                        new SqlResultDTO(-200, "User account cannot be found with the email: " + email));
            }

            final String token = UUID.randomUUID().toString();
            passwordMapper.saveTokenAndExpiration(email, token, new Timestamp(System.currentTimeMillis() + PasswordConstant.EXPIRATION));

            String resetUrl = frontEndProperties.getChange() + token;

            String subject = "Password Reset Requested - BCP";
            String msg = user.getName() + ",<br><br>" +
                    "Click the link below to reset your password, if you did not request a password reset " +
                    "you can disregard this message. The link will expire in 24 hours.<br><br>" +
                    "<a href=\"" + resetUrl + "\">Reset Password</a><br><br>" +
                    "Best regards,<br><br>PAX Support Team";

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("title", "Password Reset");
            templateData.put("message", msg);
            templateData.put("subject", subject);

            return emailService.sendTemplatedEmail(subject, "simpleMessage", templateData, email)
                    .thenApply(sesResponse -> new SqlResultDTO( 0, "Password reset email sent successfully."))
                    .exceptionally(e -> {
                        log.error("Error sending email: " + e.getMessage(), e);
                        return new SqlResultDTO(-1, "Failed to send password reset email.");
                    });
        } catch (Exception e) {
            log.error("Exception processing forgotten password: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(new SqlResultDTO(-1, "Exception occurred during the password reset process."));
        }
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
