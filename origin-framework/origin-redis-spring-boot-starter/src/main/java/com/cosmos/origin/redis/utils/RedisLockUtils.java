package com.cosmos.origin.redis.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 分布式锁工具类
 *
 * @author cosmos
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockUtils {

    private final RedissonClient redissonClient;

    /**
     * 获取锁
     *
     * @param lockKey 锁的 key
     * @return RLock 对象
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey    锁的 key
     * @param waitTime   等待时间
     * @param leaseTime  锁持有时间
     * @param timeUnit   时间单位
     * @return true 获取锁成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁失败，lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的 key
     */
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 使用分布式锁执行操作
     *
     * @param lockKey   锁的 key
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param timeUnit  时间单位
     * @param supplier  执行的操作
     * @return 操作结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Supplier<T> supplier) {
        boolean locked = tryLock(lockKey, waitTime, leaseTime, timeUnit);
        if (!locked) {
            throw new RuntimeException("获取锁失败，请稍后重试");
        }
        try {
            return supplier.get();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 使用分布式锁执行操作（无返回值）
     *
     * @param lockKey   锁的 key
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param timeUnit  时间单位
     * @param runnable  执行的操作
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit, Runnable runnable) {
        boolean locked = tryLock(lockKey, waitTime, leaseTime, timeUnit);
        if (!locked) {
            throw new RuntimeException("获取锁失败，请稍后重试");
        }
        try {
            runnable.run();
        } finally {
            unlock(lockKey);
        }
    }
}
