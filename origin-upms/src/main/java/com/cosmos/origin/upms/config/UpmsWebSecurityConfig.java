package com.cosmos.origin.upms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * UPMS Web 安全配置
 * <p>
 * 当前放行所有请求（由 Gateway 统一鉴权）。
 * TODO: 后续应接入 OAuth2 Resource Server，通过 UAA 的 /oauth2/jwks 端点验证 RSA-JWT。
 */
@Configuration
@EnableWebSecurity
public class UpmsWebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/doc.html", "/v3/api-docs/**", "/webjars/**", "/swagger-ui/**", "/favicon.ico", "/error").permitAll()
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
