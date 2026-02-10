package com.cosmos.origin.websocket.model.vo.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class FindChatMessagePageListReqVO {

    /**
     * 游标 ID
     */
    private Long lastId;
}
