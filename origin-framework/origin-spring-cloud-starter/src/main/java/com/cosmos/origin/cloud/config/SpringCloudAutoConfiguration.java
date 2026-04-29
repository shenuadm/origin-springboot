package com.cosmos.origin.cloud.config;

import com.cosmos.origin.cloud.feign.FeignRequestInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Cloud 微服务自动配置类
 * <p>
 * 统一注册 Feign、Sentinel、Nacos 等微服务组件的通用配置。
 *
 * @author 一陌千尘
 * @date 2026/04/28
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication
@EnableFeignClients(basePackages = "com.cosmos.origin")
public class SpringCloudAutoConfiguration {

    public SpringCloudAutoConfiguration() {
        log.info("==> [origin-spring-cloud-starter] 微服务自动配置已加载");
    }

    /**
     * Feign 请求拦截器：服务间调用时透传请求头
     */
    @Bean
    @ConditionalOnClass(RequestInterceptor.class)
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }
}
