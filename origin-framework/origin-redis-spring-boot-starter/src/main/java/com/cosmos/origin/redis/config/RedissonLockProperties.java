package com.cosmos.origin.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redisson 分布式锁配置属性
 *
 * @author cosmos
 */
@Data
@ConfigurationProperties(prefix = "redisson.lock")
public class RedissonLockProperties {

    /**
     * 默认锁超时时间（秒）
     */
    private long defaultTimeout = 30L;

    /**
     * 默认等待获取锁时间（秒）
     */
    private long defaultWaitTime = 10L;
}
