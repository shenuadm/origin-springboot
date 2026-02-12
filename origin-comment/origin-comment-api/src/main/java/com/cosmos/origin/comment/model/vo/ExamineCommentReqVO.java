package com.cosmos.origin.comment.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "评论审核请求参数")
public class ExamineCommentReqVO {

    @Schema(description = "评论 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评论 ID 不能为空")
    private Long id;

    @Schema(description = "评论状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评论状态不能为空")
    private Integer status;

    @Schema(description = "原因")
    private String reason;
}
