package com.cosmos.origin.jwt.handler;

import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.jwt.model.LoginRspVO;
import com.cosmos.origin.jwt.utils.JwtTokenHelper;
import com.cosmos.origin.jwt.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录成功处理
 *
 * @author 一陌千尘
 * @date 2025/07/14
 */
@Component
@Slf4j
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 从 authentication 对象中获取用户的 UserDetails 实例，这里是获取用户的用户名
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 通过用户名生成 Token
        String username = userDetails.getUsername();
        String token = jwtTokenHelper.generateToken(username);

        // 返回 Token
        LoginRspVO loginRspVO = LoginRspVO.builder()
                .token(token)
                .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        ResultUtil.ok(response, Response.success(loginRspVO));
    }
}
