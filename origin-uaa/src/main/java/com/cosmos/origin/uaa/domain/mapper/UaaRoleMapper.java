package com.cosmos.origin.uaa.domain.mapper;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.uaa.domain.dos.UaaRoleDO;
import com.cosmos.origin.uaa.domain.dos.UaaUserRoleRelDO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * UAA 角色 Mapper
 */
public interface UaaRoleMapper extends BaseMapper<UaaRoleDO> {

    /**
     * 根据用户 ID 查询角色 Key 列表
     */
    default List<String> selectRoleKeysByUserId(Long userId) {
        return selectListByQuery(QueryWrapper.create()
                        .select(UaaRoleDO::getRoleKey)
                        .from(UaaRoleDO.class)
                        .innerJoin(UaaUserRoleRelDO.class).on(UaaRoleDO::getId, UaaUserRoleRelDO::getRoleId)
                        .where(UaaUserRoleRelDO::getUserId).eq(userId)
                        .eq(UaaRoleDO::getIsDeleted, DeletedEnum.NO.getValue()))
                .stream().map(UaaRoleDO::getRoleKey).toList();
    }
}
