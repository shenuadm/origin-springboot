package com.cosmos.origin.event.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 事件驱动自动配置类
 * 负责扫描事件相关组件并导入线程池配置
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = "com.cosmos.origin.event")
@Import(ThreadPoolConfig.class)
public class EventAutoConfiguration {

    public EventAutoConfiguration() {
        log.info("========== 事件驱动模块自动配置已加载 ==========");
    }
}
