package com.cosmos.origin.websocket.model.vo.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 在线用户列表消息
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineUsersMessageVO {

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 在线用户列表
     */
    private List<OnlineUserVO> users;

    /**
     * 在线人数
     */
    private Integer onlineCount;
}
