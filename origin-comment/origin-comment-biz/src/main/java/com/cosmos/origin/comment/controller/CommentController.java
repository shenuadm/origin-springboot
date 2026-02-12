package com.cosmos.origin.comment.controller;

import com.cosmos.origin.biz.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.comment.model.vo.*;
import com.cosmos.origin.comment.service.CommentService;
import com.cosmos.origin.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
@Tag(name = "评论模块")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/publish")
    @Operation(summary = "发布评论")
    @ApiOperationLog(description = "发布评论")
    public Response<?> publishComment(@RequestBody @Validated PublishCommentReqVO publishCommentReqVO) {
        return commentService.publishComment(publishCommentReqVO);
    }

    @PostMapping("/list")
    @Operation(summary = "获取页面所有评论")
    @ApiOperationLog(description = "获取页面所有评论")
    public Response<?> findPageComments(@RequestBody @Validated FindCommentListReqVO findCommentListReqVO) {
        return commentService.findCommentList(findCommentListReqVO);
    }

    @PostMapping("/page")
    @Operation(summary = "查询评论分页数据")
    @ApiOperationLog(description = "查询评论分页数据")
    public Response<?> findCommentPageList(@RequestBody @Validated FindCommentPageListReqVO findCommentPageListReqVO) {
        return commentService.findCommentPageList(findCommentPageListReqVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "评论删除")
    @ApiOperationLog(description = "评论删除")
    public Response<?> deleteComment(@RequestBody @Validated DeleteCommentReqVO deleteCommentReqVO) {
        return commentService.deleteComment(deleteCommentReqVO);
    }

    @PostMapping("/examine")
    @Operation(summary = "评论审核")
    @ApiOperationLog(description = "评论审核")
    public Response<?> examinePass(@RequestBody @Validated ExamineCommentReqVO examineCommentReqVO) {
        return commentService.examine(examineCommentReqVO);
    }
}
