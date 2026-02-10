package com.cosmos.origin.websocket.domain.dos;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
@Table("t_chat_message")
public class ChatMessageDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

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
    private LocalDateTime createTime;

}
