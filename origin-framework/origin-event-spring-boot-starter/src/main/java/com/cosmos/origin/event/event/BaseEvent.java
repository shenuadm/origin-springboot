package com.cosmos.origin.event.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 基础事件类
 * 所有业务事件建议继承此类，方便统一管理和扩展
 *
 * @param <T> 事件携带的数据类型
 */
@Getter
public class BaseEvent<T> extends ApplicationEvent {

    /**
     * 事件携带的业务数据
     */
    private final T data;

    /**
     * 事件类型/名称，用于标识事件
     */
    private final String eventType;

    /**
     * 事件发生时间戳
     */
    private final Long eventTime;

    /**
     * 构造基础事件
     *
     * @param source    事件源对象
     * @param data      业务数据
     * @param eventType 事件类型
     */
    public BaseEvent(Object source, T data, String eventType) {
        super(source);
        this.data = data;
        this.eventType = eventType;
        this.eventTime = System.currentTimeMillis();
    }

    /**
     * 简化构造方法，默认使用类名作为事件类型
     *
     * @param source 事件源对象
     * @param data   业务数据
     */
    public BaseEvent(Object source, T data) {
        this(source, data, data.getClass().getSimpleName());
    }
}
