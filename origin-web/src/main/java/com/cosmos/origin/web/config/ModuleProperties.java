package com.cosmos.origin.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 业务模块开关配置属性
 *
 * @author cosmos
 */
@Data
@ConfigurationProperties(prefix = "origin.module")
public class ModuleProperties {

    /**
     * 管理模块开关（用户、角色、权限等）
     */
    private boolean admin = true;

    /**
     * 评论模块开关
     */
    private boolean comment = false;

    /**
     * 认证模块开关
     */
    private boolean auth = true;

    /**
     * 对象存储模块开关
     */
    private boolean oss = true;

    /**
     * WebSocket 模块开关
     */
    private boolean websocket = true;
}
