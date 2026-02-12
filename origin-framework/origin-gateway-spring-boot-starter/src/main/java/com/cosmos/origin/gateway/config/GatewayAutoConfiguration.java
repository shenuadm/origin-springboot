package com.cosmos.origin.gateway.config;

import com.cosmos.origin.gateway.filter.RateLimitFilter;
import com.cosmos.origin.gateway.properties.GatewayProperties;
import com.cosmos.origin.gateway.utils.RateLimitUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 网关自动配置类
 *
 * @author cosmos
 */
@AutoConfiguration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayAutoConfiguration {

    /**
     * 限流工具类
     */
    @Bean
    @ConditionalOnProperty(prefix = "origin.gateway.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RateLimitUtils rateLimitUtils(RedisTemplate<String, Object> redisTemplate) {
        return new RateLimitUtils(redisTemplate);
    }

    /**
     * 限流过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "origin.gateway.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RateLimitFilter rateLimitFilter(RateLimitUtils rateLimitUtils, GatewayProperties gatewayProperties) {
        return new RateLimitFilter(rateLimitUtils, gatewayProperties);
    }
}
