package com.cosmos.origin.admin.model.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "添加角色请求参数")
public class AddRoleReqVO {

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    @Schema(description = "角色标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleKey;
}
