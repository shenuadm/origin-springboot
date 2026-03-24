package com.cosmos.origin.jwt.controller;

import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.jwt.model.LoginRspVO;
import com.cosmos.origin.jwt.model.RefreshTokenReqVO;
import com.cosmos.origin.jwt.utils.JwtTokenHelper;
import com.cosmos.origin.jwt.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token 控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Token 管理")
public class TokenController {

    private final JwtTokenHelper jwtTokenHelper;

    @PostMapping("/refreshToken")
    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 换取新的 Access Token")
    public Response<LoginRspVO> refreshToken(@Valid @RequestBody RefreshTokenReqVO reqVO, HttpServletRequest request) {
        // 解析 Refresh Token 获取用户名
        String username = jwtTokenHelper.getUsernameByToken(reqVO.getRefreshToken());
        if (username == null) {
            return Response.fail("Refresh Token 无效");
        }

        // 验证 Refresh Token 是否有效
        try {
            jwtTokenHelper.validateToken(reqVO.getRefreshToken());
        } catch (Exception e) {
            return Response.fail("Refresh Token 已失效，请重新登录");
        }

        // 生成新的 Access Token
        String newToken = jwtTokenHelper.generateToken(username);
        // 生成新的 Refresh Token（也可以复用旧的，这里选择生成新的）
        String newRefreshToken = jwtTokenHelper.generateRefreshToken(username);

        // 获取用户角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            var userDetails = authentication.getPrincipal();
            if (userDetails instanceof org.springframework.security.core.userdetails.UserDetails) {
                var user = (org.springframework.security.core.userdetails.UserDetails) userDetails;
                LoginRspVO loginRspVO = LoginRspVO.builder()
                        .token(newToken)
                        .refreshToken(newRefreshToken)
                        .roles(user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList())
                        .build();
                return Response.success(loginRspVO);
            }
        }

        // 如果无法从 SecurityContext 获取用户信息，返回只有 token 的响应
        LoginRspVO loginRspVO = LoginRspVO.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .build();
        return Response.success(loginRspVO);
    }
}
