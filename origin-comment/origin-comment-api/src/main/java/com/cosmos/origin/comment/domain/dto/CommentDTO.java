package com.cosmos.origin.comment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论数据传输对象
 * <p>
 * 供其他模块使用，不包含数据库注解
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    /**
     * 评论 ID
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 评论所属的路由
     */
    private String routerUrl;

    /**
     * 回复的评论 ID
     */
    private Long replyCommentId;

    /**
     * 父评论 ID
     */
    private Long parentCommentId;

    /**
     * 状态：1-待审核 2-正常 3-审核未通过
     */
    private Integer status;

    /**
     * 原因描述
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
