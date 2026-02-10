package com.cosmos.origin.event.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池配置类
 * 用于处理异步事件任务，提升系统并发性能
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableAsync
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadPoolConfig {

    private final ThreadPoolProperties properties;

    /**
     * 创建自定义线程池，用于异步事件处理
     *
     * @return 线程池执行器
     */
    @Bean(name = "eventTaskExecutor")
    public Executor eventTaskExecutor() {
        log.info("========== 开始初始化事件驱动线程池 ==========");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(properties.getCorePoolSize());
        log.info("线程池核心线程数: {}", properties.getCorePoolSize());

        // 最大线程数
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        log.info("线程池最大线程数: {}", properties.getMaxPoolSize());

        // 队列容量
        executor.setQueueCapacity(properties.getQueueCapacity());
        log.info("线程池队列容量: {}", properties.getQueueCapacity());

        // 线程空闲时间
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());

        // 线程名前缀
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());

        // 拒绝策略：由调用线程处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(properties.getWaitForTasksToCompleteOnShutdown());

        // 设置等待终止时间
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());

        // 初始化线程池
        executor.initialize();

        log.info("========== 事件驱动线程池初始化完成 ==========");
        return executor;
    }
}
