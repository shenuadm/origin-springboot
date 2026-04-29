package com.cosmos.origin.gateway.filter;

import com.cosmos.origin.gateway.config.GatewayProperties;
import com.cosmos.origin.gateway.util.GatewayResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway 全局认证过滤器
 * <p>
 * 职责：
 * 1. 白名单路径直接放行
 * 2. 拦截非法内部请求头（仅允许内部 Feign 使用）
 * 3. 非白名单请求检查 Token 格式（Bearer 前缀）
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 内部请求头名称（仅供服务间 Feign 调用使用，外部请求携带视为非法）
     */
    public static final String X_ORIGIN_FROM_IN = "X-Origin-From-In";

    private final GatewayProperties gatewayProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        List<String> whiteList = gatewayProperties.getWhiteList();

        // 1. 白名单路径直接放行
        if (GatewayResponseUtil.isPathMatch(whiteList, path)) {
            log.debug("[Gateway] 白名单路径放行: {}", path);
            return chain.filter(exchange);
        }

        // 2. 拦截非法内部请求头
        String fromIn = exchange.getRequest().getHeaders().getFirst(X_ORIGIN_FROM_IN);
        if (fromIn != null && !fromIn.isEmpty()) {
            log.warn("[Gateway] 非法请求：外部请求携带内部请求头 {}，路径: {}", X_ORIGIN_FROM_IN, path);
            return GatewayResponseUtil.writeErrorResponse(
                    exchange.getResponse(),
                    HttpStatus.FORBIDDEN,
                    "20004",
                    "非法请求"
            );
        }

        // 3. 检查 Token 格式
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!isValidTokenFormat(authorization)) {
            log.warn("[Gateway] Token 格式非法或为空，路径: {}", path);
            return GatewayResponseUtil.writeErrorResponse(
                    exchange.getResponse(),
                    HttpStatus.UNAUTHORIZED,
                    "20002",
                    "无访问权限，请先登录"
            );
        }

        return chain.filter(exchange);
    }

    /**
     * 校验 Token 格式是否为 Bearer 开头
     *
     * @param authorization Authorization 请求头值
     * @return 是否合法
     */
    private boolean isValidTokenFormat(String authorization) {
        return authorization != null
                && authorization.startsWith("Bearer ")
                && authorization.length() > "Bearer ".length();
    }

    @Override
    public int getOrder() {
        // 认证过滤器最先执行
        return -100;
    }
}
