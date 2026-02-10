package com.cosmos.origin.websocket.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 用于在非 Spring 管理的类中，获取 Spring 容器中的 Bean
 *
 * @author 一陌千尘
 * @date 2026/02/10
 */
@Component
public class SpringContext {

    private static ApplicationContext context;

    @Autowired
    public SpringContext(ApplicationContext applicationContext) {
        SpringContext.context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
