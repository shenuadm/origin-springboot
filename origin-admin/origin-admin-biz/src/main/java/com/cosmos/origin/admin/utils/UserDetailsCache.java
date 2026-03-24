package com.cosmos.origin.admin.utils;

import com.cosmos.origin.redis.utils.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * UserDetails 缓存工具类
 * <p>
 * 用于缓存用户权限信息，减少数据库查询
 */
@Slf4j
@Component
public class UserDetailsCache {

    private static final String CACHE_KEY_PREFIX = "user:details:";

    private final RedisCacheUtil redisCacheUtil;

    /**
     * 缓存过期时间（分钟）
     */
    @Value("${jwt.userDetailsCacheExpireTime:30}")
    private Long cacheExpireTime;

    @Autowired
    public UserDetailsCache(RedisCacheUtil redisCacheUtil) {
        this.redisCacheUtil = redisCacheUtil;
        log.info("UserDetails 缓存功能已启用");
    }

    /**
     * 获取缓存的 UserDetails
     *
     * @param username 用户名
     * @return UserDetails，如果不存在返回 null
     */
    public UserDetails get(String username) {
        try {
            String key = CACHE_KEY_PREFIX + username;
            return redisCacheUtil.get(key);
        } catch (Exception e) {
            log.warn("获取用户缓存失败: {}", username, e);
            return null;
        }
    }

    /**
     * 缓存 UserDetails
     *
     * @param username    用户名
     * @param userDetails 用户详情
     */
    public void put(String username, UserDetails userDetails) {
        try {
            String key = CACHE_KEY_PREFIX + username;
            redisCacheUtil.set(key, userDetails, cacheExpireTime, TimeUnit.MINUTES);
            log.debug("缓存用户权限信息: {}", username);
        } catch (Exception e) {
            log.warn("缓存用户权限信息失败: {}", username, e);
        }
    }

    /**
     * 移除缓存的 UserDetails
     *
     * @param username 用户名
     */
    public void evict(String username) {
        try {
            String key = CACHE_KEY_PREFIX + username;
            redisCacheUtil.delete(key);
            log.debug("清除用户权限缓存: {}", username);
        } catch (Exception e) {
            log.warn("清除用户权限缓存失败: {}", username, e);
        }
    }

    /**
     * 判断缓存是否存在
     *
     * @param username 用户名
     * @return true 存在，false 不存在
     */
    public boolean contains(String username) {
        try {
            String key = CACHE_KEY_PREFIX + username;
            return redisCacheUtil.hasKey(key);
        } catch (Exception e) {
            log.warn("检查用户缓存是否存在失败: {}", username, e);
            return false;
        }
    }
}
