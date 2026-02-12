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
@Schema(name = "删除评论请求参数")
public class DeleteCommentReqVO {

    @Schema(description = "评论 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评论 ID 不能为空")
    private Long id;
}
