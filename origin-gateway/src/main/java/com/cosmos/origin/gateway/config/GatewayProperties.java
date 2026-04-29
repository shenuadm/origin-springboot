package com.cosmos.origin.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway 安全配置属性
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@Data
@Component
@ConfigurationProperties(prefix = "origin.gateway.security")
public class GatewayProperties {

    /**
     * 白名单路径（免认证）
     */
    private List<String> whiteList = new ArrayList<>();

    /**
     * 是否启用认证过滤器
     */
    private boolean authEnabled = true;

    /**
     * 是否启用 SQL 注入过滤器
     */
    private boolean sqlInjectionEnabled = true;
}
