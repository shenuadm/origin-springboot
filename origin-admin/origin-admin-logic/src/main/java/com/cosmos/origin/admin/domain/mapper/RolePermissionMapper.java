package com.cosmos.origin.admin.domain.mapper;

import com.cosmos.origin.admin.domain.dos.RolePermissionRelDO;
import com.cosmos.origin.common.enums.DeletedEnum;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RolePermissionMapper extends BaseMapper<RolePermissionRelDO> {

    /**
     * 根据角色 ID 集合批量查询
     *
     * @param roleIds 角色 ID 集合
     * @return 角色权限关系列表
     */
    default List<RolePermissionRelDO> selectByRoleIds(@Param("roleIds") List<Long> roleIds) {
        return selectListByQuery(QueryWrapper.create()
                .select().from(RolePermissionRelDO.class)
                .where(RolePermissionRelDO::getRoleId).in(roleIds)
                .where(RolePermissionRelDO::getIsDeleted).eq(DeletedEnum.NO.getValue()));
    }
}
