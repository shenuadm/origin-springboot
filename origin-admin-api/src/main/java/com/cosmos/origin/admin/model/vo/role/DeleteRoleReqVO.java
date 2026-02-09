package com.cosmos.origin.admin.model.vo.role;

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
@Schema(name = "删除角色请求参数")
public class DeleteRoleReqVO {

    @NotNull(message = "角色 ID 不能为空")
    @Schema(description = "角色 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
}
