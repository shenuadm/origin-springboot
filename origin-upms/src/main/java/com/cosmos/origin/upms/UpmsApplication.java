package com.cosmos.origin.upms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 统一权限管理服务启动类
 * <p>
 * 提供用户、角色、权限、部门、岗位等基础数据管理能力。
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cosmos.origin")
@SpringBootApplication(scanBasePackages = "com.cosmos.origin")
public class UpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpmsApplication.class, args);
    }
}
