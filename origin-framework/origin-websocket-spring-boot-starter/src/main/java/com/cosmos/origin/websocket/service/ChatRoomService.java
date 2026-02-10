package com.cosmos.origin.websocket.service;

import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.websocket.model.vo.chatroom.FindChatMessagePageListReqVO;
import com.cosmos.origin.websocket.model.vo.chatroom.FindChatMessagePageListRspVO;
import com.cosmos.origin.websocket.model.vo.chatroom.OnlineUserVO;

import java.util.List;

/**
 * 聊天室服务
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
public interface ChatRoomService {

    /**
     * 获取历史消息
     *
     * @param findChatMessagePageListReqVO 获取历史消息请求参数
     * @return 获取历史消息响应参数
     */
    Response<FindChatMessagePageListRspVO> findHistoryMessages(FindChatMessagePageListReqVO findChatMessagePageListReqVO);

    /**
     * 获取所有在线用户
     *
     * @return 获取所有在线用户响应参数
     */
    Response<List<OnlineUserVO>> findOnlineUsers();
}
