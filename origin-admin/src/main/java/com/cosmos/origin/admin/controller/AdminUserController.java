package com.cosmos.origin.admin.controller;

import com.cosmos.origin.admin.model.vo.user.*;
import com.cosmos.origin.admin.service.AdminUserService;
import com.cosmos.origin.biz.operationlog.aspect.ApiOperationLog;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 用户模块")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService userService;

    @PutMapping("/password/update")
    @Operation(summary = "修改用户密码")
    @ApiOperationLog(description = "修改用户密码")
    public Response<?> updatePassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        return userService.updatePassword(updateAdminUserPasswordReqVO);
    }

    @PostMapping("/user/info")
    @Operation(summary = "获取当前登录用户信息")
    @ApiOperationLog(description = "获取当前登录用户信息")
    public Response<?> findUserInfo() {
        return userService.findUserInfo();
    }

    @PostMapping("/user/page")
    @Operation(summary = "用户分页数据获取")
    @ApiOperationLog(description = "用户分页数据获取")
    public PageResponse<?> findUserPageList(@RequestBody @Validated FindUserPageListReqVO findUserPageListReqVO) {
        return userService.findUserPageList(findUserPageListReqVO);
    }

    @PostMapping("/user/add")
    @Operation(summary = "添加用户")
    @ApiOperationLog(description = "添加用户")
    public Response<?> addUser(@RequestBody @Validated AddUserReqVO addUserReqVO) {
        return userService.addUser(addUserReqVO);
    }

    @PutMapping("/user/update")
    @Operation(summary = "更新用户")
    @ApiOperationLog(description = "更新用户")
    public Response<?> updateUser(@RequestBody @Validated UpdateUserReqVO updateUserReqVO) {
        return userService.updateUser(updateUserReqVO);
    }

    @DeleteMapping("/user/delete")
    @Operation(summary = "删除用户")
    @ApiOperationLog(description = "删除用户")
    public Response<?> deleteUser(@RequestBody @Validated DeleteUserReqVO deleteUserReqVO) {
        return userService.deleteUser(deleteUserReqVO);
    }
}
