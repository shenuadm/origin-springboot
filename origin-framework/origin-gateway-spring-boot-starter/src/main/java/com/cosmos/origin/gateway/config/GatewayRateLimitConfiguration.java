package com.cosmos.origin.gateway.config;

import com.cosmos.origin.gateway.filter.RateLimitFilter;
import com.cosmos.origin.gateway.properties.GatewayProperties;
import com.cosmos.origin.gateway.utils.RateLimitUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 网关限流配置类
 * 在 DataRedisAutoConfiguration 之后执行，需要 RedisTemplate Bean 存在
 * 只在 Servlet Web 应用环境下生效（Spring MVC），不适用于 Reactive Web 应用（Spring Cloud Gateway）
 *
 * @author cosmos
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataRedisAutoConfiguration.class)
@ConditionalOnBean(RedisTemplate.class)
@ConditionalOnProperty(prefix = "origin.gateway.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
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
