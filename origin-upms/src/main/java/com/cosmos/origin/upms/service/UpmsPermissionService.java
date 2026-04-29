package com.cosmos.origin.upms.service;

import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.UpmsPermissionDO;

import java.util.List;

/**
 * UPMS 权限/菜单服务接口
 */
public interface UpmsPermissionService {

    /**
     * 获取权限菜单树
     */
    Response<List<UpmsPermissionDO>> tree();

    Response<?> add(UpmsPermissionDO permission);

    Response<?> update(UpmsPermissionDO permission);

    Response<?> delete(Long id);
}
