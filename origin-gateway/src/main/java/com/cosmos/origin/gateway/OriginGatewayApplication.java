package com.cosmos.origin.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * API Gateway 网关服务启动类
 * <p>
 * 基于 Spring Cloud Gateway 构建，提供以下能力：
 * <ul>
 *     <li>统一入口路由</li>
 *     <li>JWT 认证鉴权</li>
 *     <li>限流熔断</li>
 *     <li>负载均衡</li>
 * </ul>
 *
 * @author 一陌千尘
 * @date 2025/02/16
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class OriginGatewayApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment env = SpringApplication.run(OriginGatewayApplication.class, args).getEnvironment();
        log.info("""
                        
                        ----------------------------------------------------------
                        \t\
                        API Gateway: {} 启动成功！\s
                        \t\
                        Local URL: \thttp://localhost:{}
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"));
    }

}
