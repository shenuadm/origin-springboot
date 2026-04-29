package com.cosmos.origin.admin.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理后台业务逻辑自动装配配置
 * <p>
 * 扫描 domain、service、utils 等业务逻辑组件，不包含 Controller。
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.cosmos.origin.admin")
public class AdminLogicAutoConfiguration {
}
