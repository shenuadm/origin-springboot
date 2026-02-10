package com.cosmos.origin.admin.event.subscriber;

import com.cosmos.origin.admin.event.UserOperationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户操作事件监听器
 * 演示如何使用自定义线程池处理异步事件
 */
@Slf4j
@Component
public class UserOperationEventListener {

    /**
     * 监听用户操作事件 - 同步处理
     * 可以用于需要立即处理的业务逻辑
     *
     * @param event 用户操作事件
     */
    @EventListener
    public void handleUserOperationSync(UserOperationEvent event) {
        UserOperationEvent.UserOperationData data = event.getData();
        log.info("【同步处理】用户操作事件 - 用户ID: {}, 用户名: {}, 操作类型: {}, 描述: {}",
                data.getUserId(), data.getUsername(), data.getOperationType(), data.getDescription());

        // 这里可以添加同步处理逻辑，例如：
        // 1. 数据校验
        // 2. 权限检查
        // 3. 必须立即完成的业务操作
    }

    /**
     * 监听用户操作事件 - 异步处理（使用自定义线程池）
     * 用于耗时的业务逻辑，不阻塞主流程
     *
     * @param event 用户操作事件
     */
    @Async("eventTaskExecutor")
    @EventListener
    public void handleUserOperationAsync(UserOperationEvent event) {
        UserOperationEvent.UserOperationData data = event.getData();
        log.info("【异步处理】用户操作事件 - 线程: {}, 用户ID: {}, 用户名: {}, 操作类型: {}, 描述: {}",
                Thread.currentThread().getName(),
                data.getUserId(), data.getUsername(), data.getOperationType(), data.getDescription());

        try {
            // 模拟耗时操作
            Thread.sleep(1000);

            // 这里可以添加异步处理逻辑，例如：
            // 1. 发送通知（邮件、短信、站内信）
            // 2. 记录审计日志
            // 3. 数据统计分析
            // 4. 调用第三方服务
            // 5. 缓存更新

            log.info("【异步处理完成】用户操作事件处理成功 - 用户ID: {}", data.getUserId());
        } catch (Exception e) {
            log.error("【异步处理失败】用户操作事件处理异常 - 用户ID: {}, 错误信息: {}",
                    data.getUserId(), e.getMessage(), e);
        }
    }
}
