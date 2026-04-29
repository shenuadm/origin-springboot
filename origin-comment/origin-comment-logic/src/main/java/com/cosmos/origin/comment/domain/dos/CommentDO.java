package com.cosmos.origin.comment.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_comment")
public class CommentDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
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
     * 状态：1-待审核 1-正常 3-审核未通过
     */
    private Integer status;

    /**
     * 原因描述
     */
    private String reason;
}
