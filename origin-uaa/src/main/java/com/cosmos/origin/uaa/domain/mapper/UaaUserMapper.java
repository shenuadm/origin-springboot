package com.cosmos.origin.uaa.domain.mapper;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.uaa.domain.dos.UaaUserDO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * UAA 用户 Mapper
 */
public interface UaaUserMapper extends BaseMapper<UaaUserDO> {

    /**
     * 根据用户名查询用户
     */
    default UaaUserDO findByUsername(String username) {
        return selectOneByQuery(QueryWrapper.create()
                .eq(UaaUserDO::getUsername, username)
                .eq(UaaUserDO::getIsDeleted, DeletedEnum.NO.getValue()));
    }
}
