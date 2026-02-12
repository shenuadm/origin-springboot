package com.cosmos.origin.gateway.config;

import com.cosmos.origin.gateway.filter.RateLimitFilter;
import com.cosmos.origin.gateway.properties.GatewayProperties;
import com.cosmos.origin.gateway.utils.RateLimitUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 网关限流配置类
 * 在 RedisAutoConfiguration 之后执行，需要 RedisTemplate Bean 存在
 *
 * @author cosmos
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisTemplate.class)
@ConditionalOnProperty(prefix = "origin.gateway.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GatewayRateLimitConfiguration {

    /**
     * 限流工具类
     */
    @Bean
    public RateLimitUtils rateLimitUtils(RedisTemplate<String, Object> redisTemplate) {
        return new RateLimitUtils(redisTemplate);
    }

    /**
     * 限流过滤器
     */
    @Bean
    public RateLimitFilter rateLimitFilter(RateLimitUtils rateLimitUtils, GatewayProperties gatewayProperties) {
        return new RateLimitFilter(rateLimitUtils, gatewayProperties);
    }
}
