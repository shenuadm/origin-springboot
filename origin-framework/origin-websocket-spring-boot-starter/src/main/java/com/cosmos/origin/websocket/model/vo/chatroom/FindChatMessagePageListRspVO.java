package com.cosmos.origin.websocket.model.vo.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 获取历史消息
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindChatMessagePageListRspVO {

    /**
     * 聊天消息
     */
    private List<ChatMessageVO> messages;

    /**
     * 是否还有下一页
     */
    private Boolean hasMore;
}
