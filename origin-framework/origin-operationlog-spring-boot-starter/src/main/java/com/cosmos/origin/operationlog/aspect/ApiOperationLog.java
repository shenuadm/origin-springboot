package com.cosmos.origin.operationlog.aspect;

import java.lang.annotation.*;

/**
 * API 操作日志注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ApiOperationLog {

    /**
     * API 功能描述
     *
     * @return API 功能描述
     */
    String description() default "";
}
