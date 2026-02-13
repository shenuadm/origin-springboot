package com.cosmos.origin.admin.domain.mapper;

import com.cosmos.origin.admin.domain.dos.RoleDO;
import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.common.enums.StatusEnum;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;

/**
 * 角色 Mapper
 *
 * @author 一陌千尘
 * @date 2025/11/05
 */
public interface RoleMapper extends BaseMapper<RoleDO> {

    /**
     * 查询所有被启用的角色
     *
     * @return 角色列表
     */
    default List<RoleDO> selectEnabledList() {
        return selectListByQuery(QueryWrapper.create()
                .eq(RoleDO::getStatus, StatusEnum.ENABLE.getValue())
                .eq(RoleDO::getIsDeleted, DeletedEnum.NO.getValue()));
    }
}
