package com.cosmos.origin.gateway.config;

import com.cosmos.origin.gateway.properties.GatewayProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 网关自动配置类
 * 在 Spring Boot DataRedisAutoConfiguration 之后执行，确保 RedisTemplate 已创建
 * 只在 Servlet Web 应用环境下生效（Spring MVC），不适用于 Reactive Web 应用（Spring Cloud Gateway）
 *
 * @author cosmos
 */
@AutoConfiguration
@AutoConfigureAfter(DataRedisAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
@ComponentScan(basePackages = "com.cosmos.origin.gateway")
@EnableConfigurationProperties(GatewayProperties.class)
@Import({GatewayRateLimitConfiguration.class})
public class GatewayAutoConfiguration {
}
