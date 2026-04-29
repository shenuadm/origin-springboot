package com.cosmos.origin.upms.domain.mapper;

import com.cosmos.origin.common.enums.DeletedEnum;
import com.cosmos.origin.upms.domain.dos.UpmsUserDO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

/**
 * UPMS 用户 Mapper
 */
public interface UpmsUserMapper extends BaseMapper<UpmsUserDO> {

    default UpmsUserDO findByUsername(String username) {
        return selectOneByQuery(QueryWrapper.create()
                .eq(UpmsUserDO::getUsername, username)
                .eq(UpmsUserDO::getIsDeleted, DeletedEnum.NO.getValue()));
    }
}
