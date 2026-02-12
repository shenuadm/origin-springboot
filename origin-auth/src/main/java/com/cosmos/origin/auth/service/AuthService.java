package com.cosmos.origin.auth.service;

import com.cosmos.origin.redis.utils.RedisCacheUtils;
import com.cosmos.origin.redis.utils.RedisLockUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 * 提供登录尝试限制、账号锁定等安全功能
 *
 * @author cosmos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisCacheUtils redisCacheUtils;
    private final RedisLockUtils redisLockUtils;

    private static final String LOGIN_ATTEMPT_KEY_PREFIX = "login:attempt:";
    private static final String ACCOUNT_LOCK_KEY_PREFIX = "account:lock:";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    /**
     * 记录登录尝试
     *
     * @param username 用户名
     * @return 当前尝试次数
     */
    public int recordLoginAttempt(String username) {
        String key = LOGIN_ATTEMPT_KEY_PREFIX + username;
        Long attempts = redisCacheUtils.increment(key, 1);
        // 设置过期时间（1小时）
        redisCacheUtils.expire(key, 1, TimeUnit.HOURS);
        log.warn("用户 [{}] 登录失败，当前尝试次数: {}", username, attempts);
        return attempts != null ? attempts.intValue() : 0;
    }

    /**
     * 检查账号是否被锁定
     *
     * @param username 用户名
     * @return true 已被锁定
     */
    public boolean isAccountLocked(String username) {
        String lockKey = ACCOUNT_LOCK_KEY_PREFIX + username;
        Boolean locked = redisCacheUtils.hasKey(lockKey);
        return locked != null && locked;
    }

    /**
     * 获取账号锁定剩余时间（分钟）
     *
     * @param username 用户名
     * @return 剩余锁定时间，未锁定返回 0
     */
    public long getLockRemainingMinutes(String username) {
        String lockKey = ACCOUNT_LOCK_KEY_PREFIX + username;
        Long expire = redisCacheUtils.getExpire(lockKey);
        return expire != null && expire > 0 ? expire / 60 : 0;
    }

    /**
     * 锁定账号
     *
     * @param username 用户名
     */
    public void lockAccount(String username) {
        String lockKey = ACCOUNT_LOCK_KEY_PREFIX + username;
        redisCacheUtils.set(lockKey, "locked", LOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        // 清除登录尝试记录
        redisCacheUtils.delete(LOGIN_ATTEMPT_KEY_PREFIX + username);
        log.error("用户 [{}] 登录失败次数过多，账号已被锁定 {} 分钟", username, LOCK_DURATION_MINUTES);
    }

    /**
     * 解锁账号
     *
     * @param username 用户名
     */
    public void unlockAccount(String username) {
        String lockKey = ACCOUNT_LOCK_KEY_PREFIX + username;
        redisCacheUtils.delete(lockKey);
        redisCacheUtils.delete(LOGIN_ATTEMPT_KEY_PREFIX + username);
        log.info("用户 [{}] 账号已解锁", username);
    }

    /**
     * 检查并处理登录失败
     *
     * @param username 用户名
     * @return 错误信息，如果返回 null 表示未达到锁定条件
     */
    public String handleLoginFailure(String username) {
        // 检查是否已锁定
        if (isAccountLocked(username)) {
            long remainingMinutes = getLockRemainingMinutes(username);
            return String.format("账号已被锁定，请 %d 分钟后重试", remainingMinutes);
        }

        // 记录失败次数
        int attempts = recordLoginAttempt(username);

        // 检查是否需要锁定
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            lockAccount(username);
            return String.format("登录失败次数过多，账号已被锁定 %d 分钟", LOCK_DURATION_MINUTES);
        }

        return String.format("登录失败，还剩 %d 次尝试机会", MAX_LOGIN_ATTEMPTS - attempts);
    }

    /**
     * 清除登录尝试记录（登录成功时调用）
     *
     * @param username 用户名
     */
    public void clearLoginAttempts(String username) {
        redisCacheUtils.delete(LOGIN_ATTEMPT_KEY_PREFIX + username);
    }

    /**
     * 获取当前登录尝试次数
     *
     * @param username 用户名
     * @return 尝试次数
     */
    public int getCurrentAttempts(String username) {
        String key = LOGIN_ATTEMPT_KEY_PREFIX + username;
        Integer attempts = redisCacheUtils.get(key);
        return attempts != null ? attempts : 0;
    }
}
