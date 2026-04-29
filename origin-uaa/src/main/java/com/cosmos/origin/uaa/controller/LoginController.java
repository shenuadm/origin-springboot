package com.cosmos.origin.uaa.controller;

import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.uaa.config.UaaProperties;
import com.cosmos.origin.uaa.domain.dos.UaaUserDO;
import com.cosmos.origin.uaa.domain.mapper.UaaRoleMapper;
import com.cosmos.origin.uaa.domain.mapper.UaaUserMapper;
import com.cosmos.origin.uaa.model.LoginReqVO;
import com.cosmos.origin.uaa.model.LoginRspVO;
import com.cosmos.origin.uaa.model.RefreshTokenReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UAA 登录控制器
 * <p>
 * 提供 REST 风格的登录和 Token 刷新接口，兼容现有前端调用方式。
 *
 * @author 一陌千尘
 * @date 2026/04/29
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "UAA 认证")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UaaProperties uaaProperties;
    private final UaaUserMapper userMapper;
    private final UaaRoleMapper roleMapper;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Response<LoginRspVO> login(@RequestBody @Valid LoginReqVO reqVO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(reqVO.getUsername(), reqVO.getPassword())
        );

        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UaaUserDO user = userMapper.findByUsername(username);
        Long userId = user != null ? user.getId() : null;

        String accessToken = generateToken(username, userId, roles, uaaProperties.getToken().getAccessTokenTtl());
        String refreshToken = generateToken(username, userId, roles, uaaProperties.getToken().getRefreshTokenTtl());

        log.debug("用户 [{}] 登录成功，签发 Access Token", username);

        return Response.success(LoginRspVO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(uaaProperties.getToken().getAccessTokenTtl())
                .roles(roles)
                .build());
    }

    @PostMapping("/refreshToken")
    @Operation(summary = "刷新 Token")
    public Response<LoginRspVO> refreshToken(@RequestBody @Valid RefreshTokenReqVO reqVO) {
        Jwt refreshJwt;
        try {
            refreshJwt = jwtDecoder.decode(reqVO.getRefreshToken());
        } catch (JwtException e) {
            log.warn("Refresh Token 无效或已过期: {}", e.getMessage());
            return Response.fail("Refresh Token 无效或已过期，请重新登录");
        }

        String username = refreshJwt.getSubject();
        UaaUserDO user = userMapper.findByUsername(username);
        if (user == null) {
            return Response.fail("用户不存在");
        }

        List<String> roles = roleMapper.selectRoleKeysByUserId(user.getId());
        if (roles.isEmpty()) {
            roles = List.of("ROLE_USER");
        }

        String accessToken = generateToken(username, user.getId(), roles, uaaProperties.getToken().getAccessTokenTtl());
        String newRefreshToken = generateToken(username, user.getId(), roles, uaaProperties.getToken().getRefreshTokenTtl());

        log.debug("用户 [{}] 刷新 Token 成功", username);

        return Response.success(LoginRspVO.builder()
                .token(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(uaaProperties.getToken().getAccessTokenTtl())
                .roles(roles)
                .build());
    }

    private String generateToken(String username, Long userId, List<String> roles, Long ttlSeconds) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer(uaaProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .subject(username)
                .id(UUID.randomUUID().toString())
                .claim("scope", String.join(" ", roles));

        if (userId != null) {
            claimsBuilder.claim("userId", userId);
        }

        JwtClaimsSet claims = claimsBuilder.build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
