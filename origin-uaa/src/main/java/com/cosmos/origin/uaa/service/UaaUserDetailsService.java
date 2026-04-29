package com.cosmos.origin.uaa.service;

import com.cosmos.origin.uaa.domain.dos.UaaUserDO;
import com.cosmos.origin.uaa.domain.mapper.UaaRoleMapper;
import com.cosmos.origin.uaa.domain.mapper.UaaUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * UAA 用户详情服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UaaUserDetailsService implements UserDetailsService {

    private final UaaUserMapper userMapper;
    private final UaaRoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UaaUserDO user = userMapper.findByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        List<String> roleKeys = roleMapper.selectRoleKeysByUserId(user.getId());
        String[] authorities = roleKeys.toArray(new String[0]);

        log.debug("加载用户 [{}] 权限: {}", username, roleKeys);

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
