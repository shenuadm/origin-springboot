package com.cosmos.origin.admin.domain.mapper;

import com.cosmos.origin.admin.domain.dos.PermissionDO;
import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.enums.StatusEnum;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

public interface PermissionMapper extends BaseMapper<PermissionDO> {

    /**
     * 查询所有被启用的权限
     *
     * @return 权限列表
     */
    default List<PermissionDO> selectAppEnabledList() {
        return selectListByQuery(QueryWrapper.create()
                .select().from(PermissionDO.class)
                .where(PermissionDO::getStatus).eq(StatusEnum.ENABLE.getValue())
                .where(PermissionDO::getIsDeleted).eq(DeletedEnum.NO.getValue()));
    }
}
