package com.cosmos.origin.comment.api;

import com.cosmos.origin.comment.domain.dto.CommentDTO;
import com.cosmos.origin.comment.model.vo.DeleteCommentReqVO;
import com.cosmos.origin.comment.model.vo.ExamineCommentReqVO;
import com.cosmos.origin.comment.model.vo.FindCommentPageListReqVO;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 评论管理 Feign API
 * <p>
 * 供其他模块调用评论管理功能
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@FeignClient(name = "origin-comment")
@Tag(name = "评论管理 Feign 接口")
public interface CommentAdminFeignApi {

    @PostMapping("/comment/admin/page")
    @Operation(summary = "管理端：获取评论分页列表")
    PageResponse<?> findCommentPageList(@RequestBody @Validated FindCommentPageListReqVO reqVO);

    @PostMapping("/comment/admin/delete")
    @Operation(summary = "管理端：删除评论")
    Response<?> deleteComment(@RequestBody @Validated DeleteCommentReqVO reqVO);

    @PostMapping("/comment/admin/examine")
    @Operation(summary = "管理端：审核评论")
    Response<?> examineComment(@RequestBody @Validated ExamineCommentReqVO reqVO);

    @PostMapping("/comment/admin/detail")
    @Operation(summary = "获取评论详情")
    Response<CommentDTO> getCommentDetail(@RequestBody Long commentId);
}
