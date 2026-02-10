package com.cosmos.origin.event.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池配置属性类
 */
@Data
@ConfigurationProperties(prefix = "origin.event.thread-pool")
public class ThreadPoolProperties {

    /**
     * 核心线程数，默认为 CPU 核心数
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数，默认为 CPU 核心数的 2 倍
     */
    private Integer maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 队列容量，默认为 200
     */
    private Integer queueCapacity = 200;

    /**
     * 线程空闲时间（秒），默认为 60 秒
     */
    private Integer keepAliveSeconds = 60;

    /**
     * 线程名前缀，默认为 "event-executor-"
     */
    private String threadNamePrefix = "event-executor-";

    /**
     * 是否等待任务完成后再关闭线程池，默认为 true
     */
    private Boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * 等待任务完成的超时时间（秒），默认为 60 秒
     */
    private Integer awaitTerminationSeconds = 60;
}
