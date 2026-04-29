package com.cosmos.origin.upms.controller;

import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.upms.api.UpmsUserFeignApi;
import com.cosmos.origin.upms.service.UpmsUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UPMS 用户管理控制器
 */
@RestController
@RequestMapping("/upms/user")
@RequiredArgsConstructor
@Tag(name = "UPMS 用户管理")
public class UpmsUserController implements UpmsUserFeignApi {

    private final UpmsUserService userService;

    @Override
    @GetMapping("/detail")
    @Operation(summary = "根据用户名查询用户详情")
    public Response<?> findByUsername(@RequestParam String username) {
        return userService.findByUsername(username);
    }

    @PostMapping("/page")
    @Operation(summary = "用户分页列表")
    @ApiOperationLog(description = "查询用户分页列表")
    public PageResponse<?> page(@RequestParam(defaultValue = "1") Long current,
                                @RequestParam(defaultValue = "10") Long size,
                                @RequestParam(required = false) String nickname) {
        return userService.page(current, size, nickname);
    }

    @PostMapping("/assign/roles")
    @Operation(summary = "分配用户角色")
    @ApiOperationLog(description = "分配用户角色")
    public Response<?> assignRoles(@RequestParam Long userId,
                                   @RequestBody List<Long> roleIds) {
        return userService.assignRoles(userId, roleIds);
    }

    @PostMapping("/assign/departments")
    @Operation(summary = "分配用户部门")
    @ApiOperationLog(description = "分配用户部门")
    public Response<?> assignDepartments(@RequestParam Long userId,
                                         @RequestBody List<Long> departmentIds,
                                         @RequestParam(required = false) Long primaryDepartmentId) {
        return userService.assignDepartments(userId, departmentIds, primaryDepartmentId);
    }

    @PostMapping("/assign/positions")
    @Operation(summary = "分配用户岗位")
    @ApiOperationLog(description = "分配用户岗位")
    public Response<?> assignPositions(@RequestParam Long userId,
                                       @RequestBody List<Long> positionIds) {
        return userService.assignPositions(userId, positionIds);
    }
}
