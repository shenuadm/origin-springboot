package com.cosmos.origin.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.data.redis.autoconfigure.DataRedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Redisson 自动配置类
 * 提供 RedissonClient 配置
 *
 * @author cosmos
 */
@AutoConfiguration
@EnableConfigurationProperties(RedissonLockProperties.class)
public class RedissonAutoConfiguration {

    /**
     * 配置 RedissonClient
     * 使用单节点模式，复用已有的 Redis 连接配置
     */
    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedissonClient redissonClient(DataRedisProperties redisProperties) {
        // 从 RedisProperties 获取连接信息
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();

        // 构建连接 URL
        Config config = new Config();
        String connectionUrl = "redis://" + host + ":" + port;
        config.useSingleServer()
                .setAddress(connectionUrl)
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase())
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(10);

        return Redisson.create(config);
    }
}
