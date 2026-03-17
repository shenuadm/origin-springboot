package com.cosmos.origin.comment.service;

import com.cosmos.origin.comment.model.vo.DeleteCommentReqVO;
import com.cosmos.origin.comment.model.vo.ExamineCommentReqVO;
import com.cosmos.origin.comment.model.vo.FindCommentPageListReqVO;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;

/**
 * 评论管理服务接口
 * <p>
 * 定义评论管理相关的业务操作接口
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public interface AdminCommentServiceApi {

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
