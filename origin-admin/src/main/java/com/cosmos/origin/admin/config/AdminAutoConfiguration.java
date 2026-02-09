package com.cosmos.origin.admin.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理后台组件自动装配配置
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.cosmos.origin.admin")
public class AdminAutoConfiguration {
}
