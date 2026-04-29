package com.cosmos.origin.upms.service;

import com.cosmos.origin.common.response.PageResponse;
import com.cosmos.origin.common.response.Response;

import java.util.List;

/**
 * UPMS 角色服务接口
 */
public interface UpmsRoleService {

    PageResponse<?> page(Long current, Long size);

    Response<?> findSelectList();

    /**
     * 分配角色权限
     */
    Response<?> assignPermissions(Long roleId, List<Long> permissionIds);
}
