package com.cosmos.origin.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义文件大小校验注解
 *
 * @author 一陌千尘
 * @date 2025/11/06
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface FileSize {

    String message() default "文件大小超出限制";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 最大文件大小（字节）
     */
    long maxSize() default 10 * 1024 * 1024; // 默认10MB

    /**
     * 最小文件大小（字节）
     */
    long minSize() default 0;
}
