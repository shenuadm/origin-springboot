package com.cosmos.origin.websocket.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置类
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Configuration
@ConditionalOnWebApplication
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
     * 注册 ChatWebSocketServer 端点
     */
    @Bean
    public ChatWebSocketServer chatWebSocketServer() {
        return new ChatWebSocketServer();
    }
}
