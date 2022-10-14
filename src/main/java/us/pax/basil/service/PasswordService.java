package us.pax.basil.service;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.service.IService;

import us.pax.basil.dto.output.SqlResultDTO;

public interface PasswordService extends IService<Integer> {
	SqlResultDTO savePassword();
	SqlResultDTO tokenValid(String token);
	SqlResultDTO forgotPassword(HttpServletRequest request, String userEmail);
	SqlResultDTO resetPassword(HttpServletRequest request, String password, String token);
}
