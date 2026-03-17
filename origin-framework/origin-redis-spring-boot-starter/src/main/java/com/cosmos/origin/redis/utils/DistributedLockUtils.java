package com.cosmos.origin.redis.utils;

import com.cosmos.origin.redis.config.RedissonLockProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 分布式锁工具类
 * 提供简单易用的分布式锁操作接口
 *
 * @author cosmos
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockUtils {

    private final RedissonClient redissonClient;
    private final RedissonLockProperties lockProperties;

    /**
     * 获取锁（使用默认超时时间）
     * 注意：调用此方法后需要在 finally 块中手动释放锁
     *
     * @param lockKey 锁的 key
     * @return 锁对象
     */
    public RLock lock(String lockKey) {
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock(lockProperties.getDefaultTimeout(), TimeUnit.SECONDS);
        return rLock;
    }

    /**
     * 获取锁并设置自定义超时时间
     *
     * @param lockKey   锁的 key
     * @param leaseTime 锁超时时间
     * @param timeUnit  时间单位
     * @return 锁对象
     */
    public RLock lock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock(leaseTime, timeUnit);
        return rLock;
    }

    /**
     * 尝试获取锁（使用默认等待时间和超时时间）
     *
     * @param lockKey 锁的 key
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, lockProperties.getDefaultWaitTime(), lockProperties.getDefaultTimeout(), TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁（等待指定时间后放弃）
     *
     * @param lockKey   锁的 key
     * @param waitTime  等待获取锁的时间
     * @param leaseTime 锁超时时间
     * @param timeUnit  时间单位
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) {
        try {
            RLock rLock = redissonClient.getLock(lockKey);
            return rLock.tryLock(waitTime, leaseTime, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁失败, lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的 key
     */
    public void unlock(String lockKey) {
        try {
            RLock rLock = redissonClient.getLock(lockKey);
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        } catch (IllegalMonitorStateException e) {
            log.warn("释放分布式锁失败, lockKey: {}, 可能锁已过期或未持有该锁", lockKey);
        }
    }

    /**
     * 释放锁（使用锁对象）
     *
     * @param rLock 锁对象
     */
    public void unlock(RLock rLock) {
        if (rLock != null && rLock.isHeldByCurrentThread()) {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.warn("释放分布式锁失败, 可能锁已过期或未持有该锁", e);
            }
        }
    }

    /**
     * 检查锁是否被持有
     *
     * @param lockKey 锁的 key
     * @return 是否被持有
     */
    public boolean isLocked(String lockKey) {
        RLock rLock = redissonClient.getLock(lockKey);
        return rLock.isLocked();
    }

    /**
     * 检查当前线程是否持有锁
     *
     * @param lockKey 锁的 key
     * @return 当前线程是否持有锁
     */
    public boolean isHeldByCurrentThread(String lockKey) {
        RLock rLock = redissonClient.getLock(lockKey);
        return rLock.isHeldByCurrentThread();
    }
}
