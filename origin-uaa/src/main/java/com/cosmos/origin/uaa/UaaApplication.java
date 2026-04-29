package com.cosmos.origin.uaa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 统一认证授权服务启动类
 * <p>
 * OAuth2 Authorization Server，负责 Token 颁发、用户认证、客户端管理。
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.cosmos.origin")
public class UaaApplication {

    public static void main(String[] args) {
        SpringApplication.run(UaaApplication.class, args);
    }
}
