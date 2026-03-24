package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.model.vo.role.AddRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.DeleteRoleReqVO;
import com.cosmos.origin.admin.model.vo.role.FindRolePageListReqVO;
import com.cosmos.origin.admin.model.vo.role.UpdateRoleReqVO;
import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;

/**
 * 角色管理服务接口
 * <p>
 * 定义角色相关的业务操作接口，供Controller层调用
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public interface AdminRoleServiceApi {

    /**
     * 角色分页数据获取
     *
     * @param findRolePageListReqVO 角色分页数据请求参数
     * @return {@link PageResponse} 角色分页数据
     */
    PageResponse<?> findRolePageList(FindRolePageListReqVO findRolePageListReqVO);

    /**
     * 添加角色
     *
     * @param addRoleReqVO 添加角色请求参数
     * @return {@link Response} 添加结果
     */
    Response<?> add(AddRoleReqVO addRoleReqVO);

    /**
     * 更新角色
     *
     * @param updateRoleReqVO 更新角色请求参数
     * @return {@link Response} 更新结果
     */
    Response<?> update(UpdateRoleReqVO updateRoleReqVO);

    /**
     * 删除角色
     *
     * @param deleteRoleReqVO 删除角色请求参数
     * @return {@link Response} 删除结果
     */
    Response<?> delete(DeleteRoleReqVO deleteRoleReqVO);

    /**
     * 获取角色标识的 Select 列表数据
     *
     * @return {@link Response} 角色标识的 Select 列表数据
     */
    Response<?> findRoleSelectList();
}
