package com.cosmos.origin.admin.service;

import com.cosmos.origin.admin.api.WebSocketUserServiceApi;
import com.cosmos.origin.admin.domain.dos.UserDO;
import com.cosmos.origin.admin.domain.mapper.UserMapper;
import com.cosmos.origin.admin.model.vo.websocket.UserInfoForWebSocketVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * WebSocket 用户服务 API 实现类
 *
 * @author 一陌千尘
 * @date 2026/02/11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketUserServiceApiImpl implements WebSocketUserServiceApi {

    private final UserMapper userMapper;

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息，若不存在则返回 Optional.empty()
     */
    @Override
    public Optional<UserInfoForWebSocketVO> findByUsername(String username) {
        UserDO userDO = userMapper.findByUsername(username);
        if (userDO == null) {
            return Optional.empty();
        }

        UserInfoForWebSocketVO vo = UserInfoForWebSocketVO.builder()
                .id(userDO.getId())
                .username(userDO.getUsername())
                .nickname(userDO.getNickname())
                .avatar(userDO.getAvatar())
                .build();

        return Optional.of(vo);
    }
}
