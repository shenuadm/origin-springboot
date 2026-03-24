package com.cosmos.origin.comment.controller;

import com.cosmos.origin.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.comment.model.vo.DeleteCommentReqVO;
import com.cosmos.origin.comment.model.vo.ExamineCommentReqVO;
import com.cosmos.origin.comment.model.vo.FindCommentPageListReqVO;
import com.cosmos.origin.comment.service.AdminCommentService;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论管理控制器
 * <p>
 * 提供评论审核、删除等管理功能
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/comment")
@Tag(name = "Admin 评论管理模块")
@ConditionalOnProperty(prefix = "origin.module", name = "comment", havingValue = "true")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @PostMapping("/page")
    @Operation(summary = "获取评论分页列表")
    @ApiOperationLog(description = "获取评论分页列表")
    public PageResponse<?> findCommentPageList(@RequestBody @Validated FindCommentPageListReqVO reqVO) {
        return adminCommentService.findCommentPageList(reqVO);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除评论")
    @ApiOperationLog(description = "删除评论")
    public Response<?> deleteComment(@RequestBody @Validated DeleteCommentReqVO reqVO) {
        return adminCommentService.deleteComment(reqVO);
    }

    @PostMapping("/examine")
    @Operation(summary = "审核评论")
    @ApiOperationLog(description = "审核评论")
    public Response<?> examineComment(@RequestBody @Validated ExamineCommentReqVO reqVO) {
        return adminCommentService.examineComment(reqVO);
    }
}
