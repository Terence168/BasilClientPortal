package us.pax.basil.service;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.service.IService;

import us.pax.basil.dto.output.SqlResultDTO;

public interface PasswordService extends IService<Integer> {
	SqlResultDTO forgotPassword(HttpServletRequest request, String userEmail);
	SqlResultDTO savePassword();
	SqlResultDTO resetPassword(HttpServletRequest request, String password, String token);
}
