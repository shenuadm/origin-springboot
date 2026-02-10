package com.cosmos.origin.admin.api;

import com.cosmos.origin.admin.model.vo.websocket.UserInfoForWebSocketVO;

import java.util.Optional;

/**
 * WebSocket 用户服务 API 接口
 * <p>
 * 供其他模块（如 WebSocket Starter）调用，获取用户信息
 *
 * @author 一陌千尘
 * @date 2026/02/11
 */
public interface WebSocketUserServiceApi {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息，若不存在则返回 Optional.empty()
     */
    Optional<UserInfoForWebSocketVO> findByUsername(String username);
}
