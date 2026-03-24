package com.cosmos.origin.admin.api;

import com.cosmos.origin.admin.model.vo.user.*;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户管理 Feign API
 * <p>
 * 供其他模块调用用户管理功能
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@FeignClient(name = "origin-admin")
@Tag(name = "用户管理 Feign 接口")
public interface UserAdminFeignApi {

    @PostMapping("/admin/user/page")
    @Operation(summary = "获取用户分页列表")
    PageResponse<FindUserPageListRspVO> findUserPageList(@RequestBody @Validated FindUserPageListReqVO reqVO);

    @PostMapping("/admin/user/add")
    @Operation(summary = "添加用户")
    Response<?> addUser(@RequestBody @Validated AddUserReqVO reqVO);

    @PostMapping("/admin/user/update")
    @Operation(summary = "更新用户")
    Response<?> updateUser(@RequestBody @Validated UpdateUserReqVO reqVO);

    @PostMapping("/admin/user/delete")
    @Operation(summary = "删除用户")
    Response<?> deleteUser(@RequestBody @Validated DeleteUserReqVO reqVO);

    @PostMapping("/admin/user/unlock")
    @Operation(summary = "解锁用户账号")
    Response<?> unlockUser(@RequestBody @Validated UnlockUserReqVO reqVO);
}
