package com.cosmos.origin.comment.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理后台组件自动装配配置
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.cosmos.origin.comment")
public class CommentAutoConfiguration {
}
