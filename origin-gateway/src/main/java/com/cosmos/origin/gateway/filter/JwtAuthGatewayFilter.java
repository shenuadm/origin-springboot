package com.cosmos.origin.gateway.filter;

import com.cosmos.origin.jwt.utils.JwtTokenHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证网关过滤器
 * <p>
 * 在网关层统一进行 JWT Token 校验，无需每个服务单独处理
 *
 * @author 一陌千尘
 * @date 2025/02/16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGatewayFilter implements GlobalFilter, Ordered {

    private final JwtTokenHelper jwtTokenHelper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 白名单路径 - 不需要认证
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/verification-code",
            "/doc.html",
            "/v3/api-docs/**",
            "/webjars/**",
            "/favicon.ico",
            "/.well-known/**",
            "/ws/**",
            "/actuator/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单路径直接放行
        if (isWhiteList(path)) {
            log.debug("白名单路径，直接放行: {}", path);
            return chain.filter(exchange);
        }

        // 2. 获取 Token
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            log.warn("请求缺少 Token: {}", path);
            return unauthorized(exchange.getResponse(), "缺少认证信息");
        }

        // 3. 校验 Token
        try {
            jwtTokenHelper.validateToken(token);
            String username = jwtTokenHelper.getUsernameByToken(token);
            log.debug("Token 校验成功，用户: {}", username);

            // 4. 将用户信息传递到下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Name", username)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.warn("Token 校验失败: {}", e.getMessage());
            return unauthorized(exchange.getResponse(), "认证信息无效或已过期");
        }
    }

    /**
     * 判断路径是否在白名单中
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 从请求中提取 Token
     */
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 返回 401 未授权响应
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        // 优先级较高，确保在路由之前执行
        return -100;
    }

}
