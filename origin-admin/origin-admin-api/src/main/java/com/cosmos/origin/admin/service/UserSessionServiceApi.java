package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.model.vo.session.UserSessionVO;

import java.util.List;

/**
 * 用户会话服务接口
 * <p>
 * 定义用户会话相关的业务操作接口，供Controller层调用
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
public interface UserSessionServiceApi {

    /**
     * 保存用户会话
     *
     * @param userSessionVO  用户会话信息
     * @param expireMinutes  过期时间（分钟）
     */
    void saveSession(UserSessionVO userSessionVO, Long expireMinutes);

    /**
     * 根据用户名获取会话信息
     *
     * @param username 用户名
     * @return 会话信息
     */
    UserSessionVO getSessionByUsername(String username);

    /**
     * 根据用户名获取所有设备的会话信息（多设备模式）
     *
     * @param username 用户名
     * @return 所有设备的会话信息列表
     */
    List<UserSessionVO> getAllSessionsByUsername(String username);

    /**
     * 根据 Token 获取会话信息
     *
     * @param token Token
     * @return 会话信息
     */
    UserSessionVO getSessionByToken(String token);

    /**
     * 删除用户会话
     *
     * @param username 用户名
     */
    void removeSession(String username);

    /**
     * 删除指定 Token 的会话
     *
     * @param token Token
     */
    void removeSessionByToken(String token);

    /**
     * 获取所有在线用户会话
     *
     * @return 在线用户会话列表
     */
    List<UserSessionVO> getAllOnlineSessions();

    /**
     * 强制下线指定用户
     *
     * @param username 用户名
     */
    void forceLogout(String username);

    /**
     * 刷新会话过期时间
     *
     * @param username      用户名
     * @param expireMinutes 过期时间（分钟）
     */
    void refreshSessionExpire(String username, Long expireMinutes);

    /**
     * 检查用户是否在线
     *
     * @param username 用户名
     * @return true-在线，false-离线
     */
    boolean isOnline(String username);

    /**
     * 获取用户在线设备数量
     *
     * @param username 用户名
     * @return 在线设备数量
     */
    int getOnlineDeviceCount(String username);
}
