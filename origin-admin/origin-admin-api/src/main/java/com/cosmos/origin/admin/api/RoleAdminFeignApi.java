package com.cosmos.origin.admin.api;

import com.cosmos.origin.admin.model.vo.role.AddRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.DeleteRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.FindRolePageListReqVO;
import com.cosmos.origin.admin.model.vo.role.UpdateRoleReqVO;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 角色管理 Feign API
 * <p>
 * 供其他模块调用角色管理功能
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@FeignClient(name = "origin-admin")
@Tag(name = "角色管理 Feign 接口")
public interface RoleAdminFeignApi {

    @PostMapping("/admin/role/page")
    @Operation(summary = "获取角色分页列表")
    PageResponse<?> findRolePageList(@RequestBody @Validated FindRolePageListReqVO reqVO);

    @PostMapping("/admin/role/add")
    @Operation(summary = "添加角色")
    Response<?> addRole(@RequestBody @Validated AddRoleReqVO reqVO);

    @PostMapping("/admin/role/update")
    @Operation(summary = "更新角色")
    Response<?> updateRole(@RequestBody @Validated UpdateRoleReqVO reqVO);

    @PostMapping("/admin/role/delete")
    @Operation(summary = "删除角色")
    Response<?> deleteRole(@RequestBody @Validated DeleteRoleReqVO reqVO);

    @PostMapping("/admin/role/select")
    @Operation(summary = "获取角色选择列表")
    Response<?> findRoleSelectList();
}
