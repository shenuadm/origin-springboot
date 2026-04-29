package com.cosmos.origin.web.filter;

import com.cosmos.origin.admin.enums.LoginStatusEnum;
import com.cosmos.origin.admin.service.LoginAttemptService;
import com.cosmos.origin.admin.service.LoginLogService;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.jwt.constant.JwtSecurityConstants;
import com.cosmos.origin.jwt.utils.LoginResponseUtil;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * 登录尝试检查过滤器
 * <p>
 * 在登录请求到达认证过滤器之前，检查账号是否被锁定，
 * 如果被锁定则直接返回错误信息，避免进行密码验证
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAttemptCheckFilter extends OncePerRequestFilter {

    private final LoginAttemptService loginAttemptService;
    private final LoginLogService loginLogService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        // 如果功能未开启，直接放行
        if (!loginAttemptService.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 只处理登录请求
        if (isLoginRequest(request)) {
            String username = extractUsername(request);

            if (username != null && !username.isEmpty()) {
                // 检查账号是否被锁定
                if (loginAttemptService.isLocked(username)) {
                    long lockRemainingMinutes = loginAttemptService.getLockRemainingMinutes(username);

                    log.warn("用户 [{}] 尝试登录，但账号已被锁定，剩余锁定时间: {} 分钟", username, lockRemainingMinutes);

                    // 记录锁定状态的登录日志
                    String message = String.format("登录失败次数过多，账号已被锁定，请 %d 分钟后重试", lockRemainingMinutes);
                    loginLogService.recordLoginLog(username, LoginStatusEnum.LOCKED, message, request);

                    // 直接返回锁定响应
                    writeLockedResponse(response, lockRemainingMinutes);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 写入锁定响应
     */
    private void writeLockedResponse(HttpServletResponse response, long lockRemainingMinutes) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String message = String.format("登录失败次数过多，账号已被锁定，请 %d 分钟后重试", lockRemainingMinutes);

        Map<String, Object> data = LoginResponseUtil.createLockedData(lockRemainingMinutes);
        data.put("message", message);

        Response<Map<String, Object>> resp = Response.fail(
                ResponseCodeEnum.ACCOUNT_LOCKED.getErrorCode(),
                message
        );
        resp.setData(data);

        response.getWriter().write(objectMapper.writeValueAsString(resp));
    }

    /**
     * 判断是否为登录请求
     */
    private boolean isLoginRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod()) &&
                JwtSecurityConstants.DEFAULT_LOGIN_URL.equals(request.getServletPath());
    }

    /**
     * 从请求中提取用户名
     */
    private String extractUsername(HttpServletRequest request) {
        // 优先从请求属性中获取（如果前面的过滤器已经设置）
        String username = (String) request.getAttribute(JwtSecurityConstants.LOGIN_USERNAME_ATTRIBUTE);
        if (username != null) {
            return username;
        }

        // 否则从请求参数中获取
        return request.getParameter(JwtSecurityConstants.USERNAME_PARAMETER);
    }
}
