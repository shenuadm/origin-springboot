package com.cosmos.origin.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 配置
 *
 * @author 一陌千尘
 * @date 2025/10/30
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("技术底座单体版 API 接口文档")
                        .version("1.0")
                        .description("技术底座单体版 API")
                        .termsOfService("https://doc.xiaominfo.com")
                        .contact(new Contact()
                                .name("一陌千尘")
                                .email("mchshenu@gmail.com"))
                        .license(new License().name("Apache 2.0")
                                .url("https://doc.xiaominfo.com")));
    }
}
