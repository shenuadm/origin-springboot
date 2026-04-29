package com.cosmos.origin.upms.controller;

import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.upms.domain.dos.UpmsPermissionDO;
import com.cosmos.origin.upms.service.UpmsPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UPMS 权限/菜单管理控制器
 */
@RestController
@RequestMapping("/upms/permission")
@RequiredArgsConstructor
@Tag(name = "UPMS 权限/菜单管理")
public class UpmsPermissionController {

    private final UpmsPermissionService permissionService;

    @GetMapping("/tree")
    @Operation(summary = "权限菜单树")
    @ApiOperationLog(description = "查询权限菜单树")
    public Response<List<UpmsPermissionDO>> tree() {
        return permissionService.tree();
    }

    @PostMapping("/add")
    @Operation(summary = "添加权限")
    @ApiOperationLog(description = "添加权限")
    public Response<?> add(@RequestBody UpmsPermissionDO permission) {
        return permissionService.add(permission);
    }

    @PutMapping("/update")
    @Operation(summary = "更新权限")
    @ApiOperationLog(description = "更新权限")
    public Response<?> update(@RequestBody UpmsPermissionDO permission) {
        return permissionService.update(permission);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除权限")
    @ApiOperationLog(description = "删除权限")
    public Response<?> delete(@PathVariable Long id) {
        return permissionService.delete(id);
    }
}
