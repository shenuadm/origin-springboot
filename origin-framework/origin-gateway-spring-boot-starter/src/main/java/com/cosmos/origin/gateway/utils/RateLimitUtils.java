package com.cosmos.origin.gateway.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 限流工具类
 * 基于 Redis 滑动窗口算法实现
 *
 * @author cosmos
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "rate:limit:";

    /**
     * 尝试获取访问许可
     *
     * @param clientId    客户端标识
     * @param path        请求路径
     * @param limit       限流阈值
     * @param timeWindow  时间窗口（秒）
     * @return true 允许访问，false 触发限流
     */
    public boolean tryAcquire(String clientId, String path, int limit, int timeWindow) {
        String key = RATE_LIMIT_KEY_PREFIX + clientId + ":" + path;
        long now = System.currentTimeMillis();
        long windowStart = now - (timeWindow * 1000L);

        try {
            // 使用 Redis 的 ZSet 实现滑动窗口
            // 移除窗口外的旧记录
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

            // 获取当前窗口内的请求数
            Long currentCount = redisTemplate.opsForZSet().zCard(key);

            if (currentCount != null && currentCount >= limit) {
                return false;
            }

            // 添加当前请求记录
            redisTemplate.opsForZSet().add(key, String.valueOf(now), now);

            // 设置过期时间
            redisTemplate.expire(key, timeWindow + 1, TimeUnit.SECONDS);

            return true;
        } catch (Exception e) {
            log.error("限流检查失败，key: {}", key, e);
            // 限流出错时，默认允许访问，避免影响正常业务
            return true;
        }
    }

    /**
     * 获取当前请求次数
     *
     * @param clientId   客户端标识
     * @param path       请求路径
     * @param timeWindow 时间窗口（秒）
     * @return 当前请求次数
     */
    public long getCurrentCount(String clientId, String path, int timeWindow) {
        String key = RATE_LIMIT_KEY_PREFIX + clientId + ":" + path;
        long now = System.currentTimeMillis();
        long windowStart = now - (timeWindow * 1000L);

        try {
            redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);
            Long count = redisTemplate.opsForZSet().zCard(key);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取限流计数失败，key: {}", key, e);
            return 0;
        }
    }

    /**
     * 清除限流记录
     *
     * @param clientId 客户端标识
     * @param path     请求路径
     */
    public void clearLimit(String clientId, String path) {
        String key = RATE_LIMIT_KEY_PREFIX + clientId + ":" + path;
        redisTemplate.delete(key);
    }
}
