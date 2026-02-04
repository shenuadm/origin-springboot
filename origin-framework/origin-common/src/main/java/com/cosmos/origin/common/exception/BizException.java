package com.cosmos.origin.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义业务异常
 *
 * @author 一陌千尘
 * @date 2025/10/30
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

    public BizException(BaseExceptionInterface baseExceptionInterface) {
        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }
}
