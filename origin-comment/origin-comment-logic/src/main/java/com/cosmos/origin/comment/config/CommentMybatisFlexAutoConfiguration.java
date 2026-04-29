package com.cosmos.origin.comment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 管理后台 Mybatis Flex 配置
 * 采用 AutoConfiguration 方式，支持单体自动装配
 */
@AutoConfiguration
@MapperScan("com.cosmos.origin.comment.domain.mapper")
public class CommentMybatisFlexAutoConfiguration {
}
