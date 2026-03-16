package com.cosmos.origin.websocket.service.impl;

import com.cosmos.origin.websocket.config.ChatWebSocketServer;
import com.cosmos.origin.websocket.model.vo.chatroom.OnlineUserVO;
import com.cosmos.origin.websocket.service.WebSocketSessionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * WebSocket 会话管理实现类
 *
 * @author 一陌千尘
 * @date 2026/03/16
 */
@Service
public class WebSocketSessionServiceImpl implements WebSocketSessionService {

    @Override
    public List<OnlineUserVO> getOnlineUsers() {
        return ChatWebSocketServer.getOnlineUsers();
    }
}
