package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.domain.dos.UserDO;
import com.cosmos.origin.admin.domain.mapper.UserMapper;
import com.cosmos.origin.jwt.utils.LoginResponseUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录尝试限制服务
 * <p>
 * 基于 Redis 实现登录次数限制，防止暴力破解
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;
    private final UserMapper userMapper;

    // Redis key 前缀
    private static final String LOGIN_ATTEMPT_KEY_PREFIX = "login:attempt:";
    private static final String LOGIN_LOCKED_KEY_PREFIX = "login:locked:";

    // 配置项：是否开启登录次数限制功能
    @Getter
    @Value("${login.attempt.enabled}")
    private boolean enabled;

    // 配置项：最大尝试次数
    @Value("${login.attempt.max-attempts:5}")
    private int maxAttempts;

    // 配置项：锁定时间（分钟）
    @Value("${login.attempt.lock-duration-minutes:30}")
    private int lockDurationMinutes;

    /**
     * 检查用户是否被锁定
     *
     * @param username 用户名
     * @throws LockedException 如果账号被锁定
     */
    public void checkLocked(String username) {
        // 如果功能未开启，直接跳过检查
        if (!enabled) {
            return;
        }

        String lockedKey = LOGIN_LOCKED_KEY_PREFIX + username;
        Boolean isLocked = redisTemplate.hasKey(lockedKey);

        if (isLocked) {
            Long ttl = redisTemplate.getExpire(lockedKey, TimeUnit.MINUTES);
            throw new LockedException(
                    String.format("登录失败次数过多，账号已被锁定，请 %d 分钟后重试", ttl));
        }

        // 验证用户是否存在且未被禁用
        UserDO user = userMapper.findByUsername(username);
        if (user == null) {
            // 用户不存在，仍然记录尝试次数（防止用户枚举攻击）
            loginFailed(username);
        }

        // 可以扩展检查用户状态字段（如 accountLocked, enabled 等）

    }

    /**
     * 记录登录失败
     *
     * @param username 用户名
     */
    public void loginFailed(String username) {
        // 如果功能未开启，直接跳过
        if (!enabled) {
            return;
        }

        String attemptKey = LOGIN_ATTEMPT_KEY_PREFIX + username;
        String lockedKey = LOGIN_LOCKED_KEY_PREFIX + username;

        // 增加失败次数
        Long attempts = redisTemplate.opsForValue().increment(attemptKey);

        if (attempts != null) {
            // 设置过期时间（第一次失败时设置）
            if (attempts == 1) {
                redisTemplate.expire(attemptKey, Duration.ofMinutes(lockDurationMinutes));
            }

            log.warn("用户 [{}] 登录失败，当前尝试次数: {}/{}", username, attempts, maxAttempts);

            // 达到最大尝试次数，锁定账号
            if (attempts >= maxAttempts) {
                log.warn("用户 [{}] 登录失败次数达到 {} 次，账号已被锁定 {} 分钟", username, maxAttempts, lockDurationMinutes);
                redisTemplate.opsForValue().set(lockedKey, "locked", lockDurationMinutes, TimeUnit.MINUTES);
                // 注意：删除尝试次数记录应该在打印日志之后，避免后续查询时获取不到次数
                redisTemplate.delete(attemptKey);
            }
        }
    }

    /**
     * 记录登录成功，清除失败次数
     *
     * @param username 用户名
     */
    public void loginSuccess(String username) {
        // 如果功能未开启，直接跳过
        if (!enabled) {
            return;
        }

        String attemptKey = LOGIN_ATTEMPT_KEY_PREFIX + username;
        redisTemplate.delete(attemptKey);
        log.debug("用户 [{}] 登录成功，清除失败次数记录", username);
    }

    /**
     * 获取剩余允许尝试次数
     *
     * @param username 用户名
     * @return 剩余尝试次数，-1 表示未被限制
     */
    public int getRemainingAttempts(String username) {
        // 如果功能未开启，返回 -1 表示无限制
        if (!enabled) {
            return -1;
        }

        String lockedKey = LOGIN_LOCKED_KEY_PREFIX + username;
        if (redisTemplate.hasKey(lockedKey)) {
            return 0;
        }

        String attemptKey = LOGIN_ATTEMPT_KEY_PREFIX + username;
        String attemptsStr = redisTemplate.opsForValue().get(attemptKey);
        if (attemptsStr == null) {
            return maxAttempts;
        }

        int attempts = Integer.parseInt(attemptsStr);
        return Math.max(0, maxAttempts - attempts);
    }

    /**
     * 手动解锁账号（管理员使用）
     *
     * @param username 用户名
     */
    public void unlock(String username) {
        String attemptKey = LOGIN_ATTEMPT_KEY_PREFIX + username;
        String lockedKey = LOGIN_LOCKED_KEY_PREFIX + username;

        redisTemplate.delete(attemptKey);
        redisTemplate.delete(lockedKey);

        log.info("用户 [{}] 账号已手动解锁", username);
    }

    /**
     * 检查用户是否被锁定（返回布尔值）
     *
     * @param username 用户名
     * @return true 表示被锁定
     */
    public boolean isLocked(String username) {
        // 如果功能未开启，永远返回 false（未锁定）
        if (!enabled) {
            return false;
        }

        String lockedKey = LOGIN_LOCKED_KEY_PREFIX + username;
        return redisTemplate.hasKey(lockedKey);
    }

    /**
     * 获取锁定剩余时间（分钟）
     *
     * @param username 用户名
     * @return 剩余分钟数，0表示未锁定
     */
    public long getLockRemainingMinutes(String username) {
        // 如果功能未开启，返回 0
        if (!enabled) {
            return 0;
        }

        String lockedKey = LOGIN_LOCKED_KEY_PREFIX + username;
        Long ttl = redisTemplate.getExpire(lockedKey, TimeUnit.MINUTES);
        return Math.max(0, ttl);
    }

    /**
     * 获取当前尝试次数
     *
     * @param username 用户名
     * @return 当前尝试次数
     */
    public int getCurrentAttempts(String username) {
        String attemptKey = LOGIN_ATTEMPT_KEY_PREFIX + username;
        String attemptsStr = redisTemplate.opsForValue().get(attemptKey);
        return attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
    }

    /**
     * 创建锁定状态的信息 Map
     *
     * @param lockRemainingMinutes 锁定剩余时间（分钟）
     * @return 锁定信息 Map
     */
    private Map<String, Object> createLockedInfo(long lockRemainingMinutes) {
        Map<String, Object> result = LoginResponseUtil.createLockedData(lockRemainingMinutes);
        result.put("message", String.format("登录失败次数过多，账号已被锁定，请 %d 分钟后重试", lockRemainingMinutes));
        return result;
    }

    /**
     * 获取登录尝试详细信息（返回 Map，避免循环依赖）
     *
     * @param username 用户名
     * @return 登录尝试信息 Map
     */
    public Map<String, Object> getAttemptInfo(String username) {
        // 如果功能未开启，返回空信息
        if (!enabled) {
            return LoginResponseUtil.createAttemptData(0, -1);
        }

        // 检查是否被锁定
        if (isLocked(username)) {
            long lockRemainingMinutes = getLockRemainingMinutes(username);
            return createLockedInfo(lockRemainingMinutes);
        }

        // 获取当前尝试次数
        int currentAttempts = getCurrentAttempts(username);
        int remainingAttempts = Math.max(0, maxAttempts - currentAttempts);

        Map<String, Object> result = LoginResponseUtil.createAttemptData(currentAttempts, remainingAttempts);
        result.put("message", String.format("登录失败，这是第 %d 次尝试，还有 %d 次机会，超过 %d 次后账号将被锁定 %d 分钟",
                currentAttempts, remainingAttempts, maxAttempts, lockDurationMinutes));

        return result;
    }

    /**
     * 在登录失败后获取尝试信息（已增加失败次数）
     *
     * @param username 用户名
     * @return 登录尝试信息 Map
     */
    public Map<String, Object> getAttemptInfoAfterFailure(String username) {
        // 直接调用 getAttemptInfo 方法，逻辑完全相同
        return getAttemptInfo(username);
    }
}
