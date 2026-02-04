package com.cosmos.origin.jwt.service;

import com.cosmos.origin.common.domain.dos.RoleDO;
import com.cosmos.origin.common.domain.dos.UserDO;
import com.cosmos.origin.common.domain.dos.UserRoleRelDO;
import com.cosmos.origin.common.domain.mapper.RoleMapper;
import com.cosmos.origin.common.domain.mapper.UserMapper;
import com.cosmos.origin.common.enums.ResponseCodeEnum;
import com.cosmos.origin.common.exception.BizException;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 用户详情服务实现类
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中查询
        UserDO userDO = userMapper.findByUsername(username);

        // 判断用户是否存在
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException(ResponseCodeEnum.USERNAME_NOT_FOUND.getErrorMessage());
        }

        // 查询用户角色
        List<RoleDO> roleDOS = roleMapper.selectListByQuery(QueryWrapper.create()
                .select("role_key")
                .from(RoleDO.class).as("tr")
                .innerJoin(UserRoleRelDO.class).as("tur")
                .on("tr.id = tur.role_id")
                .where("tur.user_id = " + userDO.getId()));
        if (CollectionUtils.isEmpty(roleDOS)) {
            throw new BizException(ResponseCodeEnum.USER_NOT_ROLE);
        }

        // 转数组
        List<String> roles = roleDOS.stream().map(RoleDO::getRoleKey).toList();
        String[] roleArr = roles.toArray(new String[0]);

        // authorities 用于指定角色
        return User.withUsername(userDO.getUsername())
                .password(userDO.getPassword())
                .authorities(roleArr)
                .build();
    }
}
