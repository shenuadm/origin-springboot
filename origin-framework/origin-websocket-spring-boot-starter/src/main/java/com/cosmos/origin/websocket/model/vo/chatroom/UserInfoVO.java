package com.cosmos.origin.websocket.model.vo.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoVO {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;
}
