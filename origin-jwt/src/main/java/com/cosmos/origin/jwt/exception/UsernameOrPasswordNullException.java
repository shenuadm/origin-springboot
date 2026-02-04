package com.cosmos.origin.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户名或密码为空异常
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
public class UsernameOrPasswordNullException extends AuthenticationException {
    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }
}
