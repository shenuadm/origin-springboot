package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.model.vo.role.AddRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.DeleteRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.FindRolePageListReqVO;
import com.cosmos.origin.admin.model.vo.role.UpdateRoleReqVO;
import com.cosmos.origin.common.utils.PageResponse;
import com.cosmos.origin.common.utils.Response;

public interface AdminRoleService {

    /**
     * 角色分页数据获取
     *
     * @param findRolePageListReqVO 角色分页数据请求参数
     * @return {@link PageResponse }<{@link ? }> 角色分页数据
     */
    PageResponse<?> findRolePageList(FindRolePageListReqVO findRolePageListReqVO);

    /**
     * 添加角色
     *
     * @param addRoleReqVO 添加角色请求参数
     * @return {@link Response }<{@link ? }> 添加结果
     */
    Response<?> add(AddRoleReqVO addRoleReqVO);

    /**
     * 更新角色
     *
     * @param updateRoleReqVO 更新角色请求参数
     * @return {@link Response }<{@link ? }> 更新结果
     */
    Response<?> update(UpdateRoleReqVO updateRoleReqVO);

    /**
     * 删除角色
     *
     * @param deleteRoleReqVO 删除角色请求参数
     * @return {@link Response }<{@link ? }> 删除结果
     */
    Response<?> delete(DeleteRoleReqVO deleteRoleReqVO);

    /**
     * 获取角色标识的 Select 列表数据
     *
     * @return {@link Response }<{@link ? }> 角色标识的 Select 列表数据
     */
    Response<?> findRoleSelectList();
}
