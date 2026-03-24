package com.cosmos.origin.jwt.handler;

import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.jwt.constant.JwtSecurityConstants;
import com.cosmos.origin.jwt.model.LoginRspVO;
import com.cosmos.origin.jwt.utils.JwtTokenHelper;
import com.cosmos.origin.jwt.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * 登录成功处理
 *
 * @author 一陌千尘
 * @date 2025/07/14
 */
@Component
@Slf4j
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenHelper jwtTokenHelper;

    @Value("${jwt.tokenExpireTime}")
    private Long tokenExpireTime;

    @Value("${jwt.rememberMeExpireTime:10080}")
    private Long rememberMeExpireTime;

    /**
     * 会话保存回调（由外部注入，用于保存会话到 Redis）
     * 参数：(request, token, expireMinutes)
     * -- SETTER --
     * 设置会话保存回调（用于保存会话到 Redis）
     *
     */
    @Setter
    private BiConsumer<HttpServletRequest, String> sessionSaveCallback;

    @Autowired
    public RestAuthenticationSuccessHandler(JwtTokenHelper jwtTokenHelper) {
        this.jwtTokenHelper = jwtTokenHelper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 从 authentication 对象中获取用户的 UserDetails 实例，这里是获取用户的用户名
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 通过用户名生成 Token
        String username = userDetails.getUsername();

        // 检查是否勾选了"记住我"
        Boolean rememberMe = (Boolean) request.getAttribute(JwtSecurityConstants.REMEMBER_ME_ATTRIBUTE);
        String token;
        String refreshToken;
        Long expireMinutes;
        if (Boolean.TRUE.equals(rememberMe)) {
            // 记住我：使用记住我 token 过期时间（默认 7 天）
            token = jwtTokenHelper.generateRememberMeToken(username);
            refreshToken = jwtTokenHelper.generateRefreshToken(username);
            expireMinutes = rememberMeExpireTime;
        } else {
            // 正常登录：使用默认过期时间
            token = jwtTokenHelper.generateToken(username);
            refreshToken = jwtTokenHelper.generateRefreshToken(username);
            expireMinutes = tokenExpireTime;
        }

        // 保存 token 到请求属性，供回调使用
        request.setAttribute("LOGIN_TOKEN", token);
        request.setAttribute(JwtSecurityConstants.TOKEN_EXPIRE_MINUTES_ATTRIBUTE, expireMinutes);

        // 调用会话保存回调
        if (sessionSaveCallback != null) {
            try {
                sessionSaveCallback.accept(request, token);
            } catch (Exception e) {
                log.error("保存用户会话失败", e);
            }
        }

        // 返回 Token
        LoginRspVO loginRspVO = LoginRspVO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        ResultUtil.ok(response, Response.success(loginRspVO));
    }
}
