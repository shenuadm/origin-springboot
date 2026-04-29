package com.cosmos.origin.uaa.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * UAA 模块配置
 */
@Configuration
@MapperScan("com.cosmos.origin.uaa.domain.mapper")
@EnableConfigurationProperties(UaaProperties.class)
public class UaaConfig {
}
