package com.cosmos.origin.event.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 事件发布工具类
 * 封装 Spring 的 ApplicationEventPublisher，提供统一的事件发布入口
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发布事件
     *
     * @param event 事件对象
     */
    public void publishEvent(ApplicationEvent event) {
        try {
            log.debug("发布事件: {}", event.getClass().getSimpleName());
            applicationEventPublisher.publishEvent(event);
            log.debug("事件发布成功: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("事件发布失败: {}, 错误信息: {}", event.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * 发布事件（支持任意对象作为事件）
     *
     * @param eventObject 事件对象（可以不继承 ApplicationEvent）
     */
    public void publishEvent(Object eventObject) {
        try {
            log.debug("发布事件对象: {}", eventObject.getClass().getSimpleName());
            applicationEventPublisher.publishEvent(eventObject);
            log.debug("事件对象发布成功: {}", eventObject.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("事件对象发布失败: {}, 错误信息: {}", eventObject.getClass().getSimpleName(), e.getMessage(), e);
        }
    }
}
