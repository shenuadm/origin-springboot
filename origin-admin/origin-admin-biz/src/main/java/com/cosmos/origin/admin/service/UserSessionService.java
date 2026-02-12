package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.model.vo.session.UserSessionVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 用户会话服务
 * <p>
 * 负责管理用户登录会话，将会话信息保存到 Redis
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Redis key 前缀
    private static final String USER_SESSION_KEY_PREFIX = "user:session:";
    private static final String USER_TOKEN_KEY_PREFIX = "user:token:";
    private static final String USER_TOKENS_SET_KEY_PREFIX = "user:tokens:"; // 多设备登录时，保存用户的所有 token

    /**
     * 登录策略配置：single-单设备登录（互踢），multiple-多设备登录
     */
    @Value("${login.session.strategy}")
    private String loginStrategy;

    /**
     * 保存用户会话到 Redis
     * <p>
     * 支持两种登录策略：
     * - single：单设备登录（互踢），新登录会使旧 token 失效
     * - multiple：多设备登录，允许同一用户同时在多个设备登录
     *
     * @param userSessionVO 用户会话信息
     * @param expireMinutes 过期时间（分钟）
     */
    public void saveSession(UserSessionVO userSessionVO, Long expireMinutes) {
        try {
            String username = userSessionVO.getUsername();
            String token = userSessionVO.getToken();

            if ("single".equalsIgnoreCase(loginStrategy)) {
                // 单设备登录策略：删除旧的 token 和 session
                saveSingleDeviceSession(username, token, userSessionVO, expireMinutes);
            } else {
                // 多设备登录策略（默认）：允许多个 token 同时有效
                saveMultipleDeviceSession(username, token, userSessionVO, expireMinutes);
            }

            log.info("用户 [{}] 登录会话已保存到 Redis，策略：{}，过期时间：{} 分钟", username, loginStrategy, expireMinutes);
        } catch (Exception e) {
            log.error("保存用户会话失败", e);
        }
    }

    /**
     * 单设备登录策略：保存会话
     */
    private void saveSingleDeviceSession(String username, String token, UserSessionVO userSessionVO, Long expireMinutes) throws JsonProcessingException {
        // 1. 先获取旧的会话信息，删除旧的 token 映射
        UserSessionVO oldSession = getSessionByUsername(username);
        if (oldSession != null && oldSession.getToken() != null) {
            String oldTokenKey = USER_TOKEN_KEY_PREFIX + oldSession.getToken();
            redisTemplate.delete(oldTokenKey);
            log.info("用户 [{}] 重新登录，已删除旧 token 映射（单设备策略）", username);
        }

        String sessionJson = objectMapper.writeValueAsString(userSessionVO);

        // 2. 保存 username -> session 映射（单设备只保存最新的一个 session）
        String userSessionKey = USER_SESSION_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(userSessionKey, sessionJson, expireMinutes, TimeUnit.MINUTES);

        // 3. 保存 token -> username 映射
        String tokenKey = USER_TOKEN_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, username, expireMinutes, TimeUnit.MINUTES);
    }

    /**
     * 多设备登录策略：保存会话
     */
    private void saveMultipleDeviceSession(String username, String token, UserSessionVO userSessionVO, Long expireMinutes) throws JsonProcessingException {
        String sessionJson = objectMapper.writeValueAsString(userSessionVO);

        // 1. 保存 token -> session 映射（多设备时，每个 token 对应一个独立的 session）
        String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(tokenSessionKey, sessionJson, expireMinutes, TimeUnit.MINUTES);

        // 2. 保存 token -> username 映射
        String tokenKey = USER_TOKEN_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, username, expireMinutes, TimeUnit.MINUTES);

        // 3. 将 token 添加到用户的 token 集合中（用于管理该用户的所有设备）
        String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
        redisTemplate.opsForSet().add(userTokensSetKey, token);
        redisTemplate.expire(userTokensSetKey, expireMinutes, TimeUnit.MINUTES);
    }

    /**
     * 根据用户名获取会话信息
     * <p>
     * 注意：多设备登录时，此方法返回该用户的所有设备中的任意一个会话（通常是最近登录的）
     * 如需获取所有设备的会话，请使用 getAllSessionsByUsername()
     *
     * @param username 用户名
     * @return 会话信息，不存在则返回 null
     */
    public UserSessionVO getSessionByUsername(String username) {
        try {
            if ("single".equalsIgnoreCase(loginStrategy)) {
                // 单设备模式：直接从 user:session:{username} 获取
                String userSessionKey = USER_SESSION_KEY_PREFIX + username;
                String sessionJson = redisTemplate.opsForValue().get(userSessionKey);

                if (sessionJson == null) {
                    return null;
                }

                return objectMapper.readValue(sessionJson, UserSessionVO.class);
            } else {
                // 多设备模式：从用户的 token 集合中获取任意一个有效的 session
                String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
                Set<String> tokens = redisTemplate.opsForSet().members(userTokensSetKey);

                if (tokens == null || tokens.isEmpty()) {
                    return null;
                }

                // 返回第一个有效的 session
                for (String token : tokens) {
                    String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
                    String sessionJson = redisTemplate.opsForValue().get(tokenSessionKey);
                    if (sessionJson != null) {
                        return objectMapper.readValue(sessionJson, UserSessionVO.class);
                    }
                }

                return null;
            }
        } catch (JsonProcessingException e) {
            log.error("获取用户会话失败，反序列化异常", e);
            return null;
        }
    }

    /**
     * 根据用户名获取所有设备的会话信息（仅多设备模式有效）
     *
     * @param username 用户名
     * @return 所有设备的会话信息列表
     */
    public List<UserSessionVO> getAllSessionsByUsername(String username) {
        List<UserSessionVO> sessions = new ArrayList<>();

        try {
            if ("single".equalsIgnoreCase(loginStrategy)) {
                // 单设备模式：只有一个会话
                UserSessionVO session = getSessionByUsername(username);
                if (session != null) {
                    sessions.add(session);
                }
            } else {
                // 多设备模式：获取所有设备的会话
                String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
                Set<String> tokens = redisTemplate.opsForSet().members(userTokensSetKey);

                if (tokens != null && !tokens.isEmpty()) {
                    for (String token : tokens) {
                        String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
                        String sessionJson = redisTemplate.opsForValue().get(tokenSessionKey);
                        if (sessionJson != null) {
                            UserSessionVO session = objectMapper.readValue(sessionJson, UserSessionVO.class);
                            sessions.add(session);
                        } else {
                            // 清理已过期的 token
                            redisTemplate.opsForSet().remove(userTokensSetKey, token);
                        }
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("获取用户所有会话失败，反序列化异常", e);
        }

        return sessions;
    }

    /**
     * 根据 Token 获取会话信息
     *
     * @param token Token
     * @return 会话信息，不存在则返回 null
     */
    public UserSessionVO getSessionByToken(String token) {
        try {
            if ("single".equalsIgnoreCase(loginStrategy)) {
                // 单设备模式：token -> username -> session
                String tokenKey = USER_TOKEN_KEY_PREFIX + token;
                String username = redisTemplate.opsForValue().get(tokenKey);

                if (username == null) {
                    return null;
                }

                return getSessionByUsername(username);
            } else {
                // 多设备模式：token -> session（直接获取）
                String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
                String sessionJson = redisTemplate.opsForValue().get(tokenSessionKey);

                if (sessionJson == null) {
                    return null;
                }

                return objectMapper.readValue(sessionJson, UserSessionVO.class);
            }
        } catch (JsonProcessingException e) {
            log.error("根据 token 获取会话失败，反序列化异常", e);
            return null;
        }
    }

    /**
     * 删除用户会话（用于退出登录）
     * <p>
     * 单设备模式：删除唯一的会话
     * 多设备模式：删除所有设备的会话
     *
     * @param username 用户名
     */
    public void removeSession(String username) {
        if ("single".equalsIgnoreCase(loginStrategy)) {
            // 单设备模式：删除唯一的会话和 token 映射
            UserSessionVO session = getSessionByUsername(username);
            if (session != null && session.getToken() != null) {
                String tokenKey = USER_TOKEN_KEY_PREFIX + session.getToken();
                redisTemplate.delete(tokenKey);
            }

            String userSessionKey = USER_SESSION_KEY_PREFIX + username;
            redisTemplate.delete(userSessionKey);

            log.info("用户 [{}] 的会话已删除（单设备模式）", username);
        } else {
            // 多设备模式：删除所有设备的会话
            String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
            Set<String> tokens = redisTemplate.opsForSet().members(userTokensSetKey);

            if (tokens != null && !tokens.isEmpty()) {
                for (String token : tokens) {
                    String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
                    String tokenKey = USER_TOKEN_KEY_PREFIX + token;
                    redisTemplate.delete(tokenSessionKey);
                    redisTemplate.delete(tokenKey);
                }
            }

            // 删除 token 集合
            redisTemplate.delete(userTokensSetKey);

            log.info("用户 [{}] 的所有设备会话已删除（多设备模式）", username);
        }
    }

    /**
     * 删除指定 Token 的会话
     * <p>
     * 单设备模式：删除该用户的唯一会话
     * 多设备模式：仅删除该 token 对应的单个设备会话
     *
     * @param token Token
     */
    public void removeSessionByToken(String token) {
        String tokenKey = USER_TOKEN_KEY_PREFIX + token;
        String username = redisTemplate.opsForValue().get(tokenKey);

        if (username == null) {
            return;
        }

        if ("single".equalsIgnoreCase(loginStrategy)) {
            // 单设备模式：删除用户的唯一会话
            removeSession(username);
        } else {
            // 多设备模式：只删除该 token 对应的设备会话
            String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
            redisTemplate.delete(tokenSessionKey);
            redisTemplate.delete(tokenKey);

            // 从用户的 token 集合中移除该 token
            String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
            redisTemplate.opsForSet().remove(userTokensSetKey, token);

            log.info("用户 [{}] 的设备会话已删除（token: {}）", username, token.substring(0, Math.min(20, token.length())) + "...");
        }
    }

    /**
     * 获取所有在线用户会话
     * <p>
     * 单设备模式：每个用户一个会话
     * 多设备模式：返回所有设备的会话
     *
     * @return 在线用户会话列表
     */
    public List<UserSessionVO> getAllOnlineSessions() {
        List<UserSessionVO> sessions = new ArrayList<>();
        Set<String> keys = redisTemplate.keys(USER_SESSION_KEY_PREFIX + "*");

        if (!keys.isEmpty()) {
            for (String key : keys) {
                String sessionJson = redisTemplate.opsForValue().get(key);
                if (sessionJson != null) {
                    try {
                        UserSessionVO session = objectMapper.readValue(sessionJson, UserSessionVO.class);
                        sessions.add(session);
                    } catch (JsonProcessingException e) {
                        log.error("反序列化会话信息失败：{}", key, e);
                    }
                }
            }
        }

        return sessions;
    }

    /**
     * 强制下线指定用户
     *
     * @param username 用户名
     */
    public void forceLogout(String username) {
        removeSession(username);
        log.info("用户 [{}] 已被强制下线", username);
    }

    /**
     * 刷新会话过期时间
     * <p>
     * 单设备模式：刷新唯一的会话
     * 多设备模式：刷新所有设备的会话
     *
     * @param username      用户名
     * @param expireMinutes 过期时间（分钟）
     */
    public void refreshSessionExpire(String username, Long expireMinutes) {
        if ("single".equalsIgnoreCase(loginStrategy)) {
            // 单设备模式：刷新唯一的会话
            String userSessionKey = USER_SESSION_KEY_PREFIX + username;
            UserSessionVO session = getSessionByUsername(username);

            if (session != null) {
                redisTemplate.expire(userSessionKey, expireMinutes, TimeUnit.MINUTES);
                String tokenKey = USER_TOKEN_KEY_PREFIX + session.getToken();
                redisTemplate.expire(tokenKey, expireMinutes, TimeUnit.MINUTES);
                log.debug("用户 [{}] 的会话过期时间已刷新（单设备模式）", username);
            }
        } else {
            // 多设备模式：刷新所有设备的会话
            String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
            Set<String> tokens = redisTemplate.opsForSet().members(userTokensSetKey);

            if (tokens != null && !tokens.isEmpty()) {
                for (String token : tokens) {
                    String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
                    String tokenKey = USER_TOKEN_KEY_PREFIX + token;
                    redisTemplate.expire(tokenSessionKey, expireMinutes, TimeUnit.MINUTES);
                    redisTemplate.expire(tokenKey, expireMinutes, TimeUnit.MINUTES);
                }
                redisTemplate.expire(userTokensSetKey, expireMinutes, TimeUnit.MINUTES);
                log.debug("用户 [{}] 的所有设备会话过期时间已刷新（多设备模式）", username);
            }
        }
    }

    /**
     * 根据 token 刷新指定设备的会话过期时间（仅多设备模式有效）
     *
     * @param token         Token
     * @param expireMinutes 过期时间（分钟）
     */
    public void refreshSessionExpireByToken(String token, Long expireMinutes) {
        String tokenKey = USER_TOKEN_KEY_PREFIX + token;
        String username = redisTemplate.opsForValue().get(tokenKey);

        if (username == null) {
            return;
        }

        if ("single".equalsIgnoreCase(loginStrategy)) {
            // 单设备模式：直接刷新
            refreshSessionExpire(username, expireMinutes);
        } else {
            // 多设备模式：只刷新该 token 对应的设备会话
            String tokenSessionKey = USER_SESSION_KEY_PREFIX + token;
            redisTemplate.expire(tokenSessionKey, expireMinutes, TimeUnit.MINUTES);
            redisTemplate.expire(tokenKey, expireMinutes, TimeUnit.MINUTES);
            log.debug("用户 [{}] 的指定设备会话过期时间已刷新（token: {}）", username, token.substring(0, Math.min(20, token.length())) + "...");
        }
    }

    /**
     * 检查用户是否在线
     *
     * @param username 用户名
     * @return true-在线，false-离线
     */
    public boolean isOnline(String username) {
        if ("single".equalsIgnoreCase(loginStrategy)) {
            // 单设备模式：检查唯一的会话
            String userSessionKey = USER_SESSION_KEY_PREFIX + username;
            return redisTemplate.hasKey(userSessionKey);
        } else {
            // 多设备模式：检查是否有任意一个设备在线
            String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
            Set<String> tokens = redisTemplate.opsForSet().members(userTokensSetKey);
            return tokens != null && !tokens.isEmpty();
        }
    }

    /**
     * 获取用户在线设备数量（仅多设备模式有效）
     *
     * @param username 用户名
     * @return 在线设备数量
     */
    public int getOnlineDeviceCount(String username) {
        if ("single".equalsIgnoreCase(loginStrategy)) {
            return isOnline(username) ? 1 : 0;
        } else {
            String userTokensSetKey = USER_TOKENS_SET_KEY_PREFIX + username;
            Set<String> tokens = redisTemplate.opsForSet().members(userTokensSetKey);
            return tokens != null ? tokens.size() : 0;
        }
    }
}
