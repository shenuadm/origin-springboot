package com.cosmos.origin.admin.event;

import com.cosmos.origin.event.event.BaseEvent;
import lombok.Getter;

/**
 * 用户操作事件
 * 用于监听用户相关操作，实现业务解耦
 */
@Getter
public class UserOperationEvent extends BaseEvent<UserOperationEvent.UserOperationData> {

    public UserOperationEvent(Object source, UserOperationData data) {
        super(source, data, "UserOperation");
    }

    /**
     * 用户操作数据
     */
    @Getter
    public static class UserOperationData {
        /**
         * 用户 ID
         */
        private final Long userId;

        /**
         * 用户名
         */
        private final String username;

        /**
         * 操作类型（如：CREATE, UPDATE, DELETE）
         */
        private final String operationType;

        /**
         * 操作描述
         */
        private final String description;

        public UserOperationData(Long userId, String username, String operationType, String description) {
            this.userId = userId;
            this.username = username;
            this.operationType = operationType;
            this.description = description;
        }
    }
}
