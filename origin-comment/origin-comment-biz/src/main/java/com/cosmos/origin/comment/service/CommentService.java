package com.cosmos.origin.comment.service;

import com.cosmos.origin.comment.model.vo.*;
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
     * 查询页面所有评论
     *
     * @param findCommentListReqVO 查询评论列表请求参数
     * @return 查询评论列表响应结果
     */
    Response<?> findCommentList(FindCommentListReqVO findCommentListReqVO);

    /**
     * 查询评论分页数据
     *
     * @param findCommentPageListReqVO 查询评论分页数据请求参数
     * @return 查询评论分页数据响应结果
     */
    Response<?> findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO);

    /**
     * 删除评论
     *
     * @param deleteCommentReqVO 删除评论请求参数
     * @return 删除评论响应结果
     */
    Response<?> deleteComment(DeleteCommentReqVO deleteCommentReqVO);

    /**
     * 评论审核
     *
     * @param examineCommentReqVO 评论审核请求参数
     * @return 评论审核响应结果
     */
    Response<?> examine(ExamineCommentReqVO examineCommentReqVO);
}
