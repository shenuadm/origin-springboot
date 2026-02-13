package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.domain.dos.RoleDO;
import com.cosmos.origin.admin.domain.dos.UserDO;
import com.cosmos.origin.admin.domain.dos.UserRoleRelDO;
import com.cosmos.origin.admin.domain.mapper.RoleMapper;
import com.cosmos.origin.admin.domain.mapper.UserMapper;
import com.cosmos.origin.common.enums.DeletedEnum;
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
 * <p>
 * 负责从数据库加载用户信息和角色，供 Spring Security 使用
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
    @SuppressWarnings("unchecked")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中查询
        UserDO userDO = userMapper.findByUsername(username);

        // 判断用户是否存在
        if (Objects.isNull(userDO)) {
            throw new UsernameNotFoundException(ResponseCodeEnum.USERNAME_NOT_FOUND.getErrorMessage());
        }

        // 查询用户角色
        List<RoleDO> roleDOS = roleMapper.selectListByQuery(QueryWrapper.create()
                .select(RoleDO::getRoleKey)
                .from(RoleDO.class)
                .innerJoin(UserRoleRelDO.class)
                .on(RoleDO::getId, UserRoleRelDO::getRoleId)
                .where(UserRoleRelDO::getUserId).eq(userDO.getId())
                .eq(RoleDO::getIsDeleted, DeletedEnum.NO.getValue()));
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
