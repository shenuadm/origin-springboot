package com.cosmos.origin.jwt.handler;

import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BizException;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.jwt.constant.JwtSecurityConstants;
import com.cosmos.origin.jwt.exception.UsernameOrPasswordNullException;
import com.cosmos.origin.jwt.utils.LoginResponseUtil;
import com.cosmos.origin.jwt.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录失败处理
 * <p>
 * 支持返回详细的登录尝试信息，包括尝试次数和锁定状态
 *
 * @author 一陌千尘
 * @date 2025/07/14
 */
@Component
@Slf4j
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.warn("AuthenticationException: {}", exception.getMessage());

        // 获取登录尝试信息（由 onLoginFailure 回调设置）
        @SuppressWarnings("unchecked")
        Map<String, Object> attemptInfoMap = (Map<String, Object>) request.getAttribute(JwtSecurityConstants.LOGIN_ATTEMPT_INFO_MAP_ATTRIBUTE);

        if (exception instanceof UsernameOrPasswordNullException) {
            // 用户名或密码为空
            ResultUtil.fail(response, Response.fail(exception.getMessage()));
            return;
        }

        if (exception instanceof LockedException) {
            // 账号被锁定
            handleLockedResponse(response, attemptInfoMap);
            return;
        }

        if (exception instanceof BadCredentialsException) {
            // 用户名或密码错误
            handleBadCredentialsResponse(response, attemptInfoMap);
            return;
        }

        // 处理内部认证服务异常（如 BizException 被包装的情况）
        if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (cause instanceof BizException bizException) {
                ResultUtil.fail(response, Response.fail(bizException.getErrorCode(), bizException.getErrorMessage()));
                return;
            }
        }

        // 其他登录失败情况
        ResultUtil.fail(response, Response.fail(ResponseCodeEnum.LOGIN_FAIL));
    }

    /**
     * 处理账号锁定响应
     */
    private void handleLockedResponse(HttpServletResponse response, Map<String, Object> attemptInfoMap) throws IOException {
        String message = "登录失败次数过多，账号已被锁定";
        long lockRemainingMinutes = 0;

        if (attemptInfoMap != null) {
            message = (String) attemptInfoMap.get("message");
            lockRemainingMinutes = (Long) attemptInfoMap.getOrDefault("lockRemainingMinutes", 0L);
        }

        Map<String, Object> data = LoginResponseUtil.createLockedData(lockRemainingMinutes);

        Response<Map<String, Object>> resp = Response.fail(
                ResponseCodeEnum.ACCOUNT_LOCKED.getErrorCode(),
                message
        );
        resp.setData(data);
        ResultUtil.fail(response, resp);
    }

    /**
     * 处理用户名密码错误响应
     */
    private void handleBadCredentialsResponse(HttpServletResponse response, Map<String, Object> attemptInfoMap) throws IOException {
        if (attemptInfoMap != null) {
            // 有尝试信息，返回详细提示
            Map<String, Object> data = new HashMap<>();
            data.put("currentAttempts", attemptInfoMap.get("currentAttempts"));
            data.put("maxAttempts", attemptInfoMap.get("maxAttempts"));
            data.put("remainingAttempts", attemptInfoMap.get("remainingAttempts"));
            data.put("locked", attemptInfoMap.get("locked"));
            data.put("lockRemainingMinutes", attemptInfoMap.get("lockRemainingMinutes"));

            Response<Map<String, Object>> resp = Response.fail(
                    ResponseCodeEnum.USERNAME_OR_PWD_ERROR.getErrorCode(),
                    (String) attemptInfoMap.get("message")
            );
            resp.setData(data);
            ResultUtil.fail(response, resp);
            return;
        }

        // 没有尝试信息，返回默认提示
        ResultUtil.fail(response, Response.fail(ResponseCodeEnum.USERNAME_OR_PWD_ERROR));
    }
}
