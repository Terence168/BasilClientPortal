package us.pax.basil.service;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.service.IService;

import us.pax.basil.dto.output.SqlResultDTO;
import us.pax.basil.dto.input.PasswordRecoveryCompleteDTO;

import java.util.concurrent.CompletableFuture;

public interface PasswordService extends IService<Integer> {
	SqlResultDTO savePassword();
	SqlResultDTO tokenValid(String token);
	CompletableFuture<SqlResultDTO> forgotPasswordAsync(HttpServletRequest request, String userEmail);
	SqlResultDTO resetPassword(HttpServletRequest request, String password, String token);

	CompletableFuture<SqlResultDTO> requestPasswordRecoveryAsync(HttpServletRequest request, String userEmail);
	SqlResultDTO validatePasswordRecoveryToken(String encryptedUserId, String encryptedToken);
	SqlResultDTO completePasswordRecovery(HttpServletRequest request, PasswordRecoveryCompleteDTO dto);
}
