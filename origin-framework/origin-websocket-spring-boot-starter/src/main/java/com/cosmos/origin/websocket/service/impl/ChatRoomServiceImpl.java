package com.cosmos.origin.websocket.service.impl;

import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.websocket.config.ChatWebSocketServer;
import com.cosmos.origin.websocket.domain.dos.ChatMessageDO;
import com.cosmos.origin.websocket.domain.mapper.ChatMessageMapper;
import com.cosmos.origin.websocket.enums.ChatRoomMessageTypeEnum;
import com.cosmos.origin.websocket.model.vo.chatroom.ChatMessageVO;
import com.cosmos.origin.websocket.model.vo.chatroom.FindChatMessagePageListReqVO;
import com.cosmos.origin.websocket.model.vo.chatroom.FindChatMessagePageListRspVO;
import com.cosmos.origin.websocket.model.vo.chatroom.OnlineUserVO;
import com.cosmos.origin.websocket.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 聊天室服务实现
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatMessageMapper chatMessageMapper;

    /**
     * 获取历史消息
     *
     * @param findChatMessagePageListReqVO 查询历史消息请求参数
     * @return 查询历史消息响应参数
     */
    @Override
    public Response<FindChatMessagePageListRspVO> findHistoryMessages(FindChatMessagePageListReqVO findChatMessagePageListReqVO) {
        // 每页展示 10 条消息
        int pageSize = 10;
        // 游标消息 ID
        Long lastId = findChatMessagePageListReqVO.getLastId();

        // 查询指定页码记录
        List<ChatMessageDO> messages = Objects.isNull(lastId)
                ? chatMessageMapper.selectRecentMessages(pageSize) // 如果 lastId 为 null, 说明查询的是第一页消息
                : chatMessageMapper.selectMessagesBefore(lastId, pageSize); // 若 lastId 不为 null, 则执行分页查询

        // DO 实体类转 VO 实体类
        FindChatMessagePageListRspVO vo = null;

        if (!CollectionUtils.isEmpty(messages)) {
            // 倒序变正序
            Collections.reverse(messages);

            List<ChatMessageVO> voList = messages.stream().map(chatMessageDO -> ChatMessageVO.builder()
                    .id(chatMessageDO.getId())
                    .type(ChatRoomMessageTypeEnum.CHAT.getCode())
                    .nickname(chatMessageDO.getNickname())
                    .avatar(chatMessageDO.getAvatar())
                    .content(chatMessageDO.getContent())
                    .time(chatMessageDO.getCreateTime().format(ChatWebSocketServer.TIME_FORMATTER))
                    .build()
            ).collect(Collectors.toList());

            vo = FindChatMessagePageListRspVO.builder()
                    .messages(voList)
                    .hasMore(messages.size() >= pageSize) // 是否还有下一页
                    .build();
        }

        return Response.success(vo);
    }

    @Override
    public Response<List<OnlineUserVO>> findOnlineUsers() {
        // 获取所有在线用户
        List<OnlineUserVO> onlineUsers = ChatWebSocketServer.getOnlineUsers();
        return Response.success(CollectionUtils.isEmpty(onlineUsers) ? null : onlineUsers);
    }
}
