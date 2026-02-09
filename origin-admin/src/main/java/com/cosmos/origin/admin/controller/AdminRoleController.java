package com.cosmos.origin.admin.controller;

import com.cosmos.origin.biz.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import com.cosmos.origin.admin.model.vo.role.AddRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.DeleteRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.FindRolePageListReqVO;
import com.cosmos.origin.admin.model.vo.role.UpdateRoleReqVO;
import com.cosmos.origin.admin.service.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 角色模块")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService roleService;

    @PostMapping("/role/page")
    @Operation(summary = "角色分页数据获取")
    @ApiOperationLog(description = "角色分页数据获取")
    public PageResponse<?> findRolePageList(@RequestBody @Validated FindRolePageListReqVO findRolePageListReqVO) {
        return roleService.findRolePageList(findRolePageListReqVO);
    }

    @PostMapping("/role/add")
    @Operation(summary = "添加角色")
    @ApiOperationLog(description = "添加角色")
    public Response<?> addRole(@RequestBody @Validated AddRoleReqVO addRoleReqVO) {
        return roleService.add(addRoleReqVO);
    }

    @PutMapping("/role/update")
    @Operation(summary = "更新角色")
    @ApiOperationLog(description = "更新角色")
    public Response<?> updateRole(@RequestBody @Validated UpdateRoleReqVO updateRoleReqVO) {
        return roleService.update(updateRoleReqVO);
    }

    @DeleteMapping("/role/delete")
    @Operation(summary = "删除角色")
    @ApiOperationLog(description = "删除角色")
    public Response<?> deleteRole(@RequestBody @Validated DeleteRoleReqVO deleteRoleReqVO) {
        return roleService.delete(deleteRoleReqVO);
    }

    @PostMapping("/role/select/list")
    @Operation(summary = "角色 Select 下拉列表数据获取")
    @ApiOperationLog(description = "角色 Select 下拉列表数据获取")
    public Response<?> findRoleSelectList() {
        return roleService.findRoleSelectList();
    }
}
