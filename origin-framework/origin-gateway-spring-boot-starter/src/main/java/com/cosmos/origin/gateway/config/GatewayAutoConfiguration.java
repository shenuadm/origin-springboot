package com.cosmos.origin.gateway.config;

import com.cosmos.origin.gateway.properties.GatewayProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 网关自动配置类
 * 在 Spring Boot RedisAutoConfiguration 之后执行，确保 RedisTemplate 已创建
 *
 * @author cosmos
 */
@AutoConfiguration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ComponentScan(basePackages = "com.cosmos.origin.gateway")
@EnableConfigurationProperties(GatewayProperties.class)
@Import({GatewayRateLimitConfiguration.class})
public class GatewayAutoConfiguration {
}
