package com.cosmos.origin.admin.model.vo.role;

import com.cosmos.origin.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询角色分页数据请求参数")
public class FindRolePageListReqVO extends BasePageQuery {

    @Schema(description = "角色名称")
    private String roleName;
}
