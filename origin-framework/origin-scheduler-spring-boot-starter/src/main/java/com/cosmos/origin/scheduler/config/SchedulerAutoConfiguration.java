package com.cosmos.origin.scheduler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务自动配置类
 * 负责扫描定时任务相关组件并导入线程池配置
 */
@Slf4j
@AutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = "com.cosmos.origin.scheduler")
@Import(SchedulerExecutorConfig.class)
public class SchedulerAutoConfiguration {

    public SchedulerAutoConfiguration() {
        log.info("========== 定时任务模块自动配置已加载 ==========");
    }
}
