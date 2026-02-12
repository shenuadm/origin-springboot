package com.cosmos.origin.common.spi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * WebSocket 用户服务 SPI 接口
 * <p>
 * 供 WebSocket 模块调用获取用户信息，由业务模块（如 admin）实现
 *
 * @author 一陌千尘
 * @date 2026/02/11
 */
public interface WebSocketUserServiceProvider {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息，若不存在则返回 Optional.empty()
     */
    Optional<UserInfo> findByUsername(String username);

    /**
     * WebSocket 用户信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class UserInfo {
        /**
         * 用户 ID
         */
        private Long id;

        /**
         * 登录账号
         */
        private String username;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 头像
         */
        private String avatar;
    }
}
