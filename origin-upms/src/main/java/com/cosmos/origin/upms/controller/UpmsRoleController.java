package com.cosmos.origin.upms.controller;

import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.upms.service.UpmsRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UPMS 角色管理控制器
 */
@RestController
@RequestMapping("/upms/role")
@RequiredArgsConstructor
@Tag(name = "UPMS 角色管理")
public class UpmsRoleController {

    private final UpmsRoleService roleService;

    @PostMapping("/page")
    @Operation(summary = "角色分页列表")
    @ApiOperationLog(description = "查询角色分页列表")
    public PageResponse<?> page(@RequestParam(defaultValue = "1") Long current,
                                @RequestParam(defaultValue = "10") Long size) {
        return roleService.page(current, size);
    }

    @GetMapping("/select/list")
    @Operation(summary = "角色下拉列表")
    @ApiOperationLog(description = "查询角色下拉列表")
    public Response<?> selectList() {
        return roleService.findSelectList();
    }

    @PostMapping("/assign/permissions")
    @Operation(summary = "分配角色权限")
    @ApiOperationLog(description = "分配角色权限")
    public Response<?> assignPermissions(@RequestParam Long roleId,
                                         @RequestBody List<Long> permissionIds) {
        return roleService.assignPermissions(roleId, permissionIds);
    }
}
