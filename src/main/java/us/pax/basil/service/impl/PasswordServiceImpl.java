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
import us.pax.basil.dto.input.PasswordRecoveryCompleteDTO;
import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.entity.User;
import us.pax.basil.mapper.PasswordMapper;
import us.pax.basil.mapper.UserMapper;
import us.pax.basil.property.FrontEndProperties;
import us.pax.basil.service.PasswordService;
import us.pax.basil.service.aws.ses.EmailService;
import us.pax.basil.service.aws.ses.SESResponse;

import javax.servlet.http.HttpServletRequest;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.security.SecureRandom;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

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

    private static final int RECOVERY_TOKEN_EXPIRATION_MS = 15 * 60 * 1000;
    private static final int RECOVERY_RATE_LIMIT_WINDOW_MS = 10 * 60 * 1000;
    private static final int RECOVERY_RATE_LIMIT_MAX_REQUESTS = 2;
    private static final String GENERIC_RECOVERY_MSG = "If the account exists, a password reset link has been sent.";
    private static final String RECOVERY_SUCCESS_NOTIFY_SUBJECT = "Your password was updated - BCP";
    private static final String RECOVERY_CRYPTO_SECRET = Optional.ofNullable(System.getenv("RECOVERY_LINK_SECRET"))
            .orElse("basil-recovery-link-secret-change-in-prod");
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final ConcurrentHashMap<String, ArrayDeque<Long>> RECOVERY_REQUEST_WINDOW = new ConcurrentHashMap<>();

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
                    .thenApply(emailResponses -> {
                        if (hasSuccessfulDelivery(emailResponses)) {
                            return new SqlResultDTO(0, "Password reset email sent successfully.");
                        }
                        log.error("Password reset email delivery failed. recipient={}", email);
                        return new SqlResultDTO(-1, "Failed to send password reset email.");
                    })
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

    @Override
    public CompletableFuture<SqlResultDTO> requestPasswordRecoveryAsync(HttpServletRequest request, String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return CompletableFuture.completedFuture(new SqlResultDTO(0, GENERIC_RECOVERY_MSG));
            }

            final String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
            if (isRateLimited(normalizedEmail)) {
                log.warn("Password recovery request rate-limited for email: {}", normalizedEmail);
                return CompletableFuture.completedFuture(new SqlResultDTO(0, GENERIC_RECOVERY_MSG));
            }

            User user = userMapper.getUserByEmail(normalizedEmail);
            if (user == null) {
                return CompletableFuture.completedFuture(new SqlResultDTO(0, GENERIC_RECOVERY_MSG));
            }

            final String token = generateRecoveryToken();
            final Timestamp expiry = new Timestamp(System.currentTimeMillis() + RECOVERY_TOKEN_EXPIRATION_MS);
            passwordMapper.saveTokenAndExpirationByUser(user.getId(), token, expiry);

            String encryptedUserId = encryptForUrl(String.valueOf(user.getId()));
            String encryptedToken = encryptForUrl(token);
            String resetUrl = buildRecoveryResetUrl(request, encryptedUserId, encryptedToken);
            String subject = "Password Reset Requested - BCP";
            String message = "Hi " + user.getName() + ",<br><br>" +
                    "We received a request to reset your password.<br>" +
                    "Use the secure link below to set a new password (valid for 15 minutes):<br><br>" +
                    "<a href=\"" + resetUrl + "\">Reset Password</a><br><br>" +
                    "Security tip: do not forward this email.<br>" +
                    "If you did not request this change, please contact support immediately.";

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("title", "Password Reset");
            templateData.put("message", message);
            templateData.put("subject", subject);

            return emailService.sendTemplatedEmail(subject, "simpleMessage", templateData, user.getEmail())
                    .thenApply(emailResponses -> {
                        if (!hasSuccessfulDelivery(emailResponses)) {
                            log.error("Password recovery email delivery failed. recipient={}", user.getEmail());
                        }
                        return new SqlResultDTO(0, GENERIC_RECOVERY_MSG);
                    })
                    .exceptionally(e -> {
                        log.error("Failed to send password recovery email: {}", e.getMessage(), e);
                        return new SqlResultDTO(0, GENERIC_RECOVERY_MSG);
                    });
        } catch (Exception e) {
            log.error("Exception in requestPasswordRecoveryAsync: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(new SqlResultDTO(0, GENERIC_RECOVERY_MSG));
        }
    }

    @Override
    public SqlResultDTO validatePasswordRecoveryToken(String encryptedUserId, String encryptedToken) {
        try {
            if (encryptedUserId == null || encryptedToken == null
                    || encryptedUserId.trim().isEmpty() || encryptedToken.trim().isEmpty()) {
                return new SqlResultDTO(-1, "Invalid reset link.");
            }

            Integer userId = Integer.valueOf(decryptFromUrl(encryptedUserId));
            String token = decryptFromUrl(encryptedToken);

            Timestamp expirationTs = passwordMapper.getTokenExpirationByUser(userId, token);
            if (expirationTs == null) {
                return new SqlResultDTO(-1, "Invalid reset link.");
            }

            Timestamp currentTs = new Timestamp(System.currentTimeMillis());
            if (currentTs.after(expirationTs)) {
                return new SqlResultDTO(-1, "Reset link has expired.");
            }

            return new SqlResultDTO(0, "");
        } catch (Exception e) {
            log.error("Exception in validatePasswordRecoveryToken: {}", e.getMessage(), e);
            return new SqlResultDTO(-1, "Failed to validate reset link.");
        }
    }

    @Override
    public SqlResultDTO completePasswordRecovery(HttpServletRequest request, PasswordRecoveryCompleteDTO dto) {
        try {
            if (dto == null || dto.getEncryptedUserId() == null || dto.getEncryptedToken() == null || dto.getPassword() == null) {
                return new SqlResultDTO(-1, "Invalid reset request.");
            }

            Integer userId = Integer.valueOf(decryptFromUrl(dto.getEncryptedUserId()));
            String token = decryptFromUrl(dto.getEncryptedToken());

            User user = passwordMapper.getUserByIdAndToken(userId, token);
            if (user == null) {
                return new SqlResultDTO(-1, "Invalid reset link.");
            }

            Timestamp currentTs = new Timestamp(System.currentTimeMillis());
            if (user.getTokenExp() == null || currentTs.after(user.getTokenExp())) {
                return new SqlResultDTO(-1, "Reset link has expired.");
            }

            passwordMapper.savePasswordByUser(user.getId(), passwordEncoder.encode(dto.getPassword()));
            passwordMapper.clearTokenByUser(user.getId());

            sendPasswordChangedNotification(user, request);

            return new SqlResultDTO(0, "Password has been reset successfully.");
        } catch (Exception e) {
            log.error("Exception in completePasswordRecovery: {}", e.getMessage(), e);
            return new SqlResultDTO(-1, "Failed to reset password.");
        }
    }

    private String generateRecoveryToken() {
        byte[] random = new byte[4];
        SECURE_RANDOM.nextBytes(random);
        StringBuilder suffix = new StringBuilder();
        for (byte b : random) {
            suffix.append(String.format("%02x", b));
        }
        return UUID.randomUUID().toString() + "-" + suffix;
    }

    private String encryptForUrl(String plainText) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SECURE_RANDOM.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] payload = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, payload, 0, iv.length);
        System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload);
    }

    private String decryptFromUrl(String encryptedText) throws Exception {
        byte[] payload = Base64.getUrlDecoder().decode(encryptedText);
        if (payload.length <= GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted payload");
        }

        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encrypted = new byte[payload.length - GCM_IV_LENGTH];
        System.arraycopy(payload, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(payload, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, buildSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private SecretKeySpec buildSecretKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key = digest.digest(RECOVERY_CRYPTO_SECRET.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

    private boolean isRateLimited(String email) {
        long now = System.currentTimeMillis();
        ArrayDeque<Long> queue = RECOVERY_REQUEST_WINDOW.computeIfAbsent(email, key -> new ArrayDeque<>());
        synchronized (queue) {
            while (!queue.isEmpty() && now - queue.peekFirst() > RECOVERY_RATE_LIMIT_WINDOW_MS) {
                queue.pollFirst();
            }
            if (queue.size() >= RECOVERY_RATE_LIMIT_MAX_REQUESTS) {
                return true;
            }
            queue.offerLast(now);
            return false;
        }
    }

    private String buildRecoveryResetUrl(HttpServletRequest request, String encryptedUserId, String encryptedToken) throws Exception {
        String origin = null;
        if (request != null) {
            origin = request.getHeader("Origin");
            if (origin == null || origin.trim().isEmpty()) {
                String scheme = request.getScheme();
                String host = request.getServerName();
                int port = request.getServerPort();
                boolean useDefaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                        || ("https".equalsIgnoreCase(scheme) && port == 443);
                origin = scheme + "://" + host + (useDefaultPort ? "" : ":" + port);
            }
        }

        if (origin == null || origin.trim().isEmpty()) {
            origin = "http://localhost:9000";
        }

        origin = origin.replaceAll("/+$", "");
        return origin
                + "/reset-password?user_id=" + URLEncoder.encode(encryptedUserId, StandardCharsets.UTF_8.name())
                + "&token=" + URLEncoder.encode(encryptedToken, StandardCharsets.UTF_8.name());
    }

    private void sendPasswordChangedNotification(User user, HttpServletRequest request) {
        try {
            String subject = RECOVERY_SUCCESS_NOTIFY_SUBJECT;
            String sourceIp = request == null ? "unknown" : request.getRemoteAddr();
            String message = "Hi " + user.getName() + ",<br><br>" +
                    "Your password was successfully updated.<br>" +
                    "Time: " + new Timestamp(System.currentTimeMillis()) + "<br>" +
                    "IP: " + sourceIp + "<br><br>" +
                    "If this was not you, please contact support immediately.";

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("title", "Password Updated");
            templateData.put("message", message);
            templateData.put("subject", subject);

            emailService.sendTemplatedEmail(subject, "simpleMessage", templateData, user.getEmail())
                    .thenAccept(emailResponses -> {
                        if (!hasSuccessfulDelivery(emailResponses)) {
                            log.error("Password changed notification email delivery failed. recipient={}", user.getEmail());
                        }
                    })
                    .exceptionally(e -> {
                        log.error("Failed to send password updated notification: {}", e.getMessage(), e);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Failed to build password changed notification: {}", e.getMessage(), e);
        }
    }

    /**
     * 鍒ゆ柇閭欢鍙戦€佹槸鍚﹀瓨鍦ㄨ嚦灏戜竴鏉℃垚鍔熺粨鏋溿€?     *
     * @param responses 閭欢鍙戦€佺粨鏋滈泦鍚?     * @return true 琛ㄧず鑷冲皯涓€鏉″彂閫佹垚鍔燂紱false 琛ㄧず鍏ㄩ儴澶辫触鎴栨棤缁撴灉
     */
    private boolean hasSuccessfulDelivery(List<SESResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return false;
        }
        for (SESResponse response : responses) {
            if (response != null && response.isSuccess()) {
                return true;
            }
        }
        return false;
    }
}
