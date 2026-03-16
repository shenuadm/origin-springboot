package com.cosmos.origin.websocket.service;

import com.cosmos.origin.websocket.model.vo.chatroom.OnlineUserVO;

import java.util.List;

/**
 * WebSocket 会话管理接口
 * <p>
 * 抽象 WebSocket 相关的操作，便于业务代码与具体实现解耦
 *
 * @author 一陌千尘
 * @date 2026/03/16
 */
public interface WebSocketSessionService {

    /**
     * 获取所有在线用户
     *
     * @return 在线用户列表
     */
    List<OnlineUserVO> getOnlineUsers();
}
