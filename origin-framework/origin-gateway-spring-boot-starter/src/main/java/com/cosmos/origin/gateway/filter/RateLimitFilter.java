package com.cosmos.origin.gateway.filter;

import com.cosmos.origin.gateway.properties.GatewayProperties;
import com.cosmos.origin.gateway.utils.RateLimitUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 限流过滤器
 *
 * @author cosmos
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter implements Filter {

    private final RateLimitUtils rateLimitUtils;
    private final GatewayProperties gatewayProperties;

    public RateLimitFilter(RateLimitUtils rateLimitUtils, GatewayProperties gatewayProperties) {
        this.rateLimitUtils = rateLimitUtils;
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientId = getClientId(httpRequest);
        String path = httpRequest.getRequestURI();

        // 检查是否在白名单中
        if (isInWhiteList(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否在黑名单中
        if (isInBlackList(clientId)) {
            writeErrorResponse(httpResponse, HttpStatus.FORBIDDEN.value(), "访问被拒绝");
            return;
        }

        // 获取该接口的限流阈值
        int limit = getPathLimit(path);

        // 限流检查
        if (!rateLimitUtils.tryAcquire(clientId, path, limit, gatewayProperties.getRateLimit().getTimeWindow())) {
            log.warn("触发限流，clientId: {}, path: {}", clientId, path);
            writeErrorResponse(httpResponse, HttpStatus.TOO_MANY_REQUESTS.value(), 
                    gatewayProperties.getRateLimit().getLimitMessage());
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * 获取客户端标识
     */
    private String getClientId(HttpServletRequest request) {
        // 优先使用用户ID，其次使用IP地址
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return "user:" + userId;
        }
        return "ip:" + getClientIp(request);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 检查是否在白名单中
     */
    private boolean isInWhiteList(String path) {
        return gatewayProperties.getWhiteList().stream()
                .anyMatch(path::startsWith);
    }

    /**
     * 检查是否在黑名单中
     */
    private boolean isInBlackList(String clientId) {
        return gatewayProperties.getBlackList().contains(clientId);
    }

    /**
     * 获取接口限流阈值
     */
    private int getPathLimit(String path) {
        return gatewayProperties.getRateLimit().getPathLimits()
                .getOrDefault(path, gatewayProperties.getRateLimit().getDefaultLimit());
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String json = String.format("{\"code\":%d,\"message\":\"%s\",\"data\":null}", status, message);
        response.getWriter().write(json);
    }
}
