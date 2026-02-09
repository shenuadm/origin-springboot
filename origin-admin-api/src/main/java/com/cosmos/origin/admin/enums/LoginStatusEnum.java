package com.cosmos.origin.admin.enums;

import lombok.Getter;

/**
 * 登录状态枚举
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@Getter
public enum LoginStatusEnum {

    /**
     * 登录成功
     */
    SUCCESS(1, "登录成功"),

    /**
     * 登录失败
     */
    FAILED(0, "登录失败"),

    /**
     * 账号被锁定
     */
    LOCKED(-1, "账号被锁定");

    private final Integer code;
    private final String description;

    LoginStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
}
