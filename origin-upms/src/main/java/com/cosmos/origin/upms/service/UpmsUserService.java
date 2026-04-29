package com.cosmos.origin.upms.service;

import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;

import java.util.List;

/**
 * UPMS 用户服务接口
 */
public interface UpmsUserService {

    /**
     * 根据用户名查询用户详情（含角色、部门、岗位）
     */
    Response<?> findByUsername(String username);

    /**
     * 用户分页列表
     */
    PageResponse<?> page(Long current, Long size, String nickname);

    /**
     * 查询用户角色列表
     */
    List<String> findRoleKeysByUserId(Long userId);

    /**
     * 查询用户部门列表
     */
    List<Long> findDepartmentIdsByUserId(Long userId);

    /**
     * 查询用户岗位列表
     */
    List<Long> findPositionIdsByUserId(Long userId);

    /**
     * 分配用户角色
     */
    Response<?> assignRoles(Long userId, List<Long> roleIds);

    /**
     * 分配用户部门
     */
    Response<?> assignDepartments(Long userId, List<Long> departmentIds, Long primaryDepartmentId);

    /**
     * 分配用户岗位
     */
    Response<?> assignPositions(Long userId, List<Long> positionIds);
}
