package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.model.vo.comment.DeleteCommentReqVO;
import com.cosmos.origin.admin.model.vo.comment.ExamineCommentReqVO;
import com.cosmos.origin.admin.model.vo.comment.FindCommentPageListReqVO;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;

public interface AdminCommentService {

    /**
     * 查询评论分页数据
     *
     * @param findCommentPageListReqVO 查询评论分页数据请求参数
     * @return 查询评论分页数据响应结果
     */
    PageResponse<?> findCommentPageList(FindCommentPageListReqVO findCommentPageListReqVO);

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
    Response<?> examineComment(ExamineCommentReqVO examineCommentReqVO);
}
