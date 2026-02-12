package com.cosmos.origin.admin.controller;

import com.cosmos.origin.admin.model.vo.comment.DeleteCommentReqVO;
import com.cosmos.origin.admin.model.vo.comment.ExamineCommentReqVO;
import com.cosmos.origin.admin.model.vo.comment.FindCommentPageListReqVO;
import com.cosmos.origin.admin.service.AdminCommentService;
import com.cosmos.origin.biz.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
@Tag(name = "Admin 评论管理模块")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @PostMapping("/page")
    @Operation(summary = "查询评论分页数据")
    @ApiOperationLog(description = "查询评论分页数据")
    public PageResponse<?> findCommentPageList(@RequestBody @Validated FindCommentPageListReqVO findCommentPageListReqVO) {
        return adminCommentService.findCommentPageList(findCommentPageListReqVO);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "评论删除")
    @ApiOperationLog(description = "评论删除")
    public Response<?> deleteComment(@RequestBody @Validated DeleteCommentReqVO deleteCommentReqVO) {
        return adminCommentService.deleteComment(deleteCommentReqVO);
    }

    @PostMapping("/examine")
    @Operation(summary = "评论审核")
    @ApiOperationLog(description = "评论审核")
    public Response<?> examineComment(@RequestBody @Validated ExamineCommentReqVO examineCommentReqVO) {
        return adminCommentService.examineComment(examineCommentReqVO);
    }
}
