package com.cosmos.origin.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 聊天室消息类型
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Getter
@AllArgsConstructor
public enum ChatRoomMessageTypeEnum {

    SYSTEM(0, "系统消息"),
    CHAT(1, "聊天消息");

    private final Integer code;
    private final String description;

}
