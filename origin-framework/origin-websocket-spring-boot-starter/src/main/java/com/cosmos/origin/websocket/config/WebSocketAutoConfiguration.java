package com.cosmos.origin.websocket.config;

import com.cosmos.origin.websocket.utils.SpringContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置类
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Configuration
@ConditionalOnWebApplication
@ComponentScan({"com.cosmos.origin.websocket.controller", "com.cosmos.origin.websocket.service"})
@Import(ChatWebSocketServer.class)
public class WebSocketAutoConfiguration {

    /**
     * 注册 WebSocket 端点（使用 @ServerEndpoint 注解声明的方式）
     * 注意：该 Bean 会扫描并注册所有带 @ServerEndpoint 注解的类
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * 注册 SpringContext 工具类
     * 用于在 WebSocket 端点中获取 Spring 容器中的 Bean
     */
    @Bean
    public SpringContext springContext() {
        return new SpringContext();
    }
}
