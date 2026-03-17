package com.cosmos.origin.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executor;

/**
 * Nacos 配置中心自动配置类
 * <p>
 * 自动启用 Nacos 配置中心，支持配置动态刷新
 *
 * @author 一陌千尘
 * @date 2025/02/16
 */
@Slf4j
@Configuration
@ConditionalOnClass(NacosConfigManager.class)
@ConditionalOnProperty(prefix = "origin.config.nacos", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({com.cosmos.origin.config.NacosConfigProperties.class, NacosConfigProperties.class})
public class OriginNacosConfigAutoConfiguration {

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @PostConstruct
    public void init() {
        log.info("Origin Nacos Config Starter 初始化完成");
        log.info("Nacos Server: {}", nacosConfigProperties.getServerAddr());
        log.info("Namespace: {}", nacosConfigProperties.getNamespace());
        log.info("Group: {}", nacosConfigProperties.getGroup());
    }

    /**
     * 配置监听器示例 - 监听配置变更
     */
    @Bean
    public NacosConfigListener nacosConfigListener() {
        return new NacosConfigListener();
    }

    /**
     * 内部类：配置监听器
     */
    @Slf4j
    public static class NacosConfigListener {

        @Autowired
        private NacosConfigManager configManager;

        @Autowired
        private NacosConfigProperties configProperties;

        @PostConstruct
        public void addListener() throws Exception {
            String dataId = configProperties.getPrefix() + "." + configProperties.getFileExtension();
            String group = configProperties.getGroup();

            configManager.getConfigService().addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String config) {
                    log.info("配置发生变更 - dataId: {}, group: {}", dataId, group);
                }
            });
        }
    }

}
