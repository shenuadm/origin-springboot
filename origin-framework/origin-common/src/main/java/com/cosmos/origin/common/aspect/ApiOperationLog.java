package com.cosmos.origin.common.aspect;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // 指定注解的保留策略，RetentionPolicy.RUNTIME 表示该注解将在运行时保留
@Target({ElementType.METHOD}) // 指定注解的目标元素，ElementType.METHOD 表示该注解只能用于方法上
@Documented // 指定被注解的元素出现在生成的Java文档中
public @interface ApiOperationLog {

    /**
     * API 功能描述
     *
     * @return API 功能描述
     */
    String description() default "";

}
