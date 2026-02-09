package com.cosmos.origin.common.config;

import com.cosmos.origin.common.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        JacksonConfig.class,
        MybatisFlexConfig.class,
        GlobalExceptionHandler.class
})
public class CommonAutoConfiguration {
}
