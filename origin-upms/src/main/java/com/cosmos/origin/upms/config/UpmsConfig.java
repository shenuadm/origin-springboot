package com.cosmos.origin.upms.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * UPMS 模块配置
 */
@Configuration
@MapperScan("com.cosmos.origin.upms.domain.mapper")
public class UpmsConfig {
}
