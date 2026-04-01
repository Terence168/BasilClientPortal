package us.pax.basil.security;

import com.paxcq.cloud.common.dto.Result;
import com.paxcq.cloud.common.enums.ResultEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.web.session.InvalidSessionStrategy;
import us.pax.basil.utils.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 会话失效统一 JSON 返回策略。
 *
 * 设计说明：
 * 1. 旧配置使用 invalidSessionUrl("/login")，会导致 API 请求被重定向到登录接口。
 * 2. 前后端分离场景下，推荐直接返回标准 JSON 错误码，由前端自行决定跳转逻辑。
 * 3. 本策略可避免“找回密码等公开接口被动触发登录跳转”的副作用。
 */
@Log4j2
public class JsonInvalidSessionStrategy implements InvalidSessionStrategy {

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.warn("Invalid session detected for request: {}", request.getRequestURI());
        ResponseUtil.out(response, Result.error(ResultEnum.SESSION_TIMEOUT));
    }
}
