package com.cosmos.origin.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway 路由配置
 * <p>
 * 定义服务路由规则，支持负载均衡
 *
 * @author 一陌千尘
 * @date 2025/02/16
 */
@Slf4j
@Configuration
public class GatewayConfig {

    /**
     * 配置路由规则
     * <p>
     * 路由规则：
     * <ul>
     *     <li>/auth/** -> origin-auth 服务</li>
     *     <li>/admin/** -> origin-admin 服务</li>
     *     <li>/comment/** -> origin-comment 服务</li>
     *     <li>/web/** -> origin-web 服务</li>
     * </ul>
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("初始化 Gateway 路由配置...");
        return builder.routes()
                // 认证服务路由
                .route("origin-auth", r -> r.path("/auth/**")
                        .uri("lb://origin-auth"))
                // 管理后台服务路由
                .route("origin-admin", r -> r.path("/admin/**")
                        .uri("lb://origin-admin"))
                // 评论服务路由
                .route("origin-comment", r -> r.path("/comment/**")
                        .uri("lb://origin-comment"))
                // Web 服务路由
                .route("origin-web", r -> r.path("/web/**")
                        .uri("lb://origin-web"))
                .build();
    }

}
