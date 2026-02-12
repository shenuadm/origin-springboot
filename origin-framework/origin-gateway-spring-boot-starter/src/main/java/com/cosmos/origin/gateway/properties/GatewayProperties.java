package com.cosmos.origin.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * 网关配置属性
 *
 * @author cosmos
 */
@Data
@ConfigurationProperties(prefix = "origin.gateway")
public class GatewayProperties {

    /**
     * 是否启用网关功能
     */
    private boolean enabled = true;

    /**
     * 限流配置
     */
    private RateLimitProperties rateLimit = new RateLimitProperties();

    /**
     * 白名单配置
     */
    private List<String> whiteList = Collections.emptyList();

    /**
     * 黑名单配置
     */
    private List<String> blackList = Collections.emptyList();

    /**
     * 限流配置属性
     */
    @Data
    public static class RateLimitProperties {
        /**
         * 是否启用限流
         */
        private boolean enabled = true;

        /**
         * 默认限流阈值（每秒请求数）
         */
        private int defaultLimit = 100;

        /**
         * 限流时间窗口（秒）
         */
        private int timeWindow = 1;

        /**
         * 超过限流阈值时的提示信息
         */
        private String limitMessage = "请求过于频繁，请稍后再试";

        /**
         * 特定接口的限流配置（key: 接口路径，value: 限流阈值）
         */
        private java.util.Map<String, Integer> pathLimits = Collections.emptyMap();
    }
}
