package com.cosmos.origin.admin.service.impl;

import com.cosmos.origin.admin.domain.dos.UserDO;
import com.cosmos.origin.admin.domain.mapper.UserMapper;
import com.cosmos.origin.common.spi.WebSocketUserServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * WebSocket 用户服务 SPI 实现类
 * <p>
 * 实现通用SPI接口，为WebSocket模块提供用户信息查询服务
 *
 * @author 一陌千尘
 * @date 2026/02/11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketUserServiceApiImpl implements WebSocketUserServiceProvider {

    private final UserMapper userMapper;

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息，若不存在则返回 Optional.empty()
     */
    @Override
    public Optional<UserInfo> findByUsername(String username) {
        UserDO userDO = userMapper.findByUsername(username);
        if (userDO == null) {
            return Optional.empty();
        }

        UserInfo userInfo = UserInfo.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .nickname(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .build();

        return Optional.of(userInfo);
    }
}
