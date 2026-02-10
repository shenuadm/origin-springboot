package com.cosmos.origin.websocket.model.vo.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天室消息
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageVO {

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    private String time;

    /**
     * 在线人数
     */
    private Integer onlineCount;
}
