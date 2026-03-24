package com.cosmos.origin.comment.api;

import com.cosmos.origin.comment.model.vo.FindCommentListReqVO;
import com.cosmos.origin.comment.model.vo.PublishCommentReqVO;
import com.cosmos.origin.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "origin-comment")
@Tag(name = "评论模块 Feign 接口")
public interface CommentFeignApi {

    @PostMapping("/comment/publish")
    @Operation(summary = "发布评论")
    Response<?> publishComment(@RequestBody @Validated PublishCommentReqVO publishCommentReqVO);

    @PostMapping("/comment/list")
    @Operation(summary = "获取页面所有评论")
    Response<?> findPageComments(@RequestBody @Validated FindCommentListReqVO findCommentListReqVO);
}
