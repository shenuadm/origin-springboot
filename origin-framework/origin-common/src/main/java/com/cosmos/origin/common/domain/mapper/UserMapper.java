package com.cosmos.origin.common.domain.mapper;

import com.cosmos.origin.common.domain.dos.UserDO;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.Objects;

public interface UserMapper extends BaseMapper<UserDO> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return {@link UserDO }  用户
     */
    default UserDO findByUsername(String username) {
        return selectOneByQuery(QueryWrapper.create()
                .eq(UserDO::getUsername, username));
    }

    /**
     * 根据用户名修改密码
     *
     * @param username 用户名
     * @param password 密码
     * @return int 更新数量
     */
    default int updatePasswordByUsername(String username, String password) {
        // 查询用户
        UserDO userDO = selectOneByQuery(QueryWrapper.create()
                .eq(UserDO::getUsername, username));
        if (Objects.isNull(userDO)) {
            return 0;
        }
        // 设置要更新的字段
        userDO.setPassword(password);
        // 更新
        return updateByQuery(userDO, true, QueryWrapper.create()
                .eq(UserDO::getUsername, username));
    }
}
