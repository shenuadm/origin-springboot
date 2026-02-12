package com.cosmos.origin.comment.service;

import com.cosmos.origin.comment.model.vo.FindCommentListReqVO;
import com.cosmos.origin.comment.model.vo.PublishCommentReqVO;
import com.cosmos.origin.common.utils.Response;

public interface CommentService {

    /**
     * 发布评论
     *
     * @param publishCommentReqVO 发布评论请求参数
     * @return 发布评论响应结果
     */
    Response<?> publishComment(PublishCommentReqVO publishCommentReqVO);

    /**
     * 查询评论列表数据
     *
     * @param findCommentListReqVO 查询评论列表请求参数
     * @return 查询评论列表响应结果
     */
    Response<?> findCommentList(FindCommentListReqVO findCommentListReqVO);
}
