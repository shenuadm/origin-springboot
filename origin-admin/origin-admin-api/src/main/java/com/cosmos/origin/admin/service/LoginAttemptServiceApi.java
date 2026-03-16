package com.cosmos.origin.admin.service;

import org.springframework.security.authentication.LockedException;

import java.util.Map;

/**
 * 登录尝试限制服务接口
 * <p>
 * 定义登录尝试限制相关的业务操作接口，供其他模块调用
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public interface LoginAttemptServiceApi {

    /**
     * 检查用户是否被锁定
     *
     * @param username 用户名
     * @throws LockedException 如果账号被锁定
     */
    void checkLocked(String username) throws LockedException;

    /**
     * 记录登录失败
     *
     * @param username 用户名
     */
    void loginFailed(String username);

    /**
     * 记录登录成功，清除失败次数
     *
     * @param username 用户名
     */
    void loginSuccess(String username);

    /**
     * 获取剩余允许尝试次数
     *
     * @param username 用户名
     * @return 剩余尝试次数，-1 表示未被限制
     */
    int getRemainingAttempts(String username);

    /**
     * 手动解锁账号
     *
     * @param username 用户名
     */
    void unlock(String username);

    /**
     * 检查用户是否被锁定（返回布尔值）
     *
     * @param username 用户名
     * @return true 表示被锁定
     */
    boolean isLocked(String username);

    /**
     * 获取锁定剩余时间（分钟）
     *
     * @param username 用户名
     * @return 剩余分钟数，0表示未锁定
     */
    long getLockRemainingMinutes(String username);

    /**
     * 获取登录尝试详细信息
     *
     * @param username 用户名
     * @return 登录尝试信息 Map
     */
    Map<String, Object> getAttemptInfo(String username);

    /**
     * 功能是否启用
     *
     * @return true 表示启用
     */
    boolean isEnabled();
}
