package com.cosmos.origin.comment.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 评论业务逻辑自动装配配置
 * <p>
 * 扫描 domain、service 等业务逻辑组件，不包含 Controller。
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.cosmos.origin.comment")
public class CommentLogicAutoConfiguration {
}
