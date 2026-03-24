package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.model.vo.user.*;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;

public interface AdminUserService extends AdminUserServiceApi {

    /**
     * 修改密码
     *
     * @param updateAdminUserPasswordReqVO 修改密码请求参数
     * @return 修改密码结果
     */
    Response<?> updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    Response<?> findUserInfo();

    /**
     * 用户分页数据获取
     *
     * @return {@link Response }<{@link ? }>
     */
    PageResponse<?> findUserPageList(FindUserPageListReqVO findUserPageListReqVO);

    /**
     * 添加用户
     *
     * @param addUserReqVO 添加用户请求参数
     * @return {@link Response }<{@link ? }> 添加用户结果
     */
    Response<?> addUser(AddUserReqVO addUserReqVO);

    /**
     * 更新用户
     *
     * @param updateUserReqVO 更新用户请求参数
     * @return {@link Response }<{@link ? }> 更新用户结果
     */
    Response<?> updateUser(UpdateUserReqVO updateUserReqVO);

    /**
     * 删除用户
     *
     * @param deleteUserReqVO 删除用户请求参数
     * @return {@link Response }<{@link ? }> 删除用户结果
     */
    Response<?> deleteUser(DeleteUserReqVO deleteUserReqVO);

    /**
     * 手动解锁用户账号（管理员使用）
     *
     * @param unlockUserReqVO 解锁用户请求参数
     * @return {@link Response }<{@link ? }> 解锁结果
     */
    Response<?> unlockUser(UnlockUserReqVO unlockUserReqVO);
}
