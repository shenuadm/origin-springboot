package com.cosmos.origin.jwt.config;

import com.cosmos.origin.jwt.utils.JwtTokenHelper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * JWT 自动配置类
 * <p>
 * 提供 JWT 工具类配置，与 Web 环境无关
 *
 * @author 一陌千尘
 * @date 2025/02/16
 */
@AutoConfiguration
public class JwtAutoConfiguration {

    /**
     * JWT Token 工具类
     */
    @Bean
    public JwtTokenHelper jwtTokenHelper() {
        return new JwtTokenHelper();
    }

}
