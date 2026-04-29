package com.cosmos.origin.upms.service;

import com.cosmos.origin.common.response.Response;
import com.cosmos.origin.upms.domain.dos.UpmsDepartmentDO;

import java.util.List;

/**
 * UPMS 部门服务接口
 */
public interface UpmsDepartmentService {

    /**
     * 获取部门树
     */
    Response<List<UpmsDepartmentDO>> tree();

    /**
     * 添加部门
     */
    Response<?> add(UpmsDepartmentDO department);

    /**
     * 更新部门
     */
    Response<?> update(UpmsDepartmentDO department);

    /**
     * 删除部门
     */
    Response<?> delete(Long id);
}
