package com.cosmos.origin.gateway.config;

import com.cosmos.origin.gateway.interceptor.AccessLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 网关 WebMvc 配置
 *
 * @author cosmos
 */
@Configuration
public class GatewayWebMvcConfiguration implements WebMvcConfigurer {

    private final AccessLogInterceptor accessLogInterceptor;

    public GatewayWebMvcConfiguration(AccessLogInterceptor accessLogInterceptor) {
        this.accessLogInterceptor = accessLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册访问日志拦截器
        registry.addInterceptor(accessLogInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/favicon.ico", "/error");
    }
}
