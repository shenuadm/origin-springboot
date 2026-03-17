package com.cosmos.origin.jwt.config;

import com.cosmos.origin.jwt.filter.JwtAuthenticationFilter;
import com.cosmos.origin.jwt.filter.TokenAuthenticationFilter;
import com.cosmos.origin.jwt.handler.RestAuthenticationFailureHandler;
import com.cosmos.origin.jwt.handler.RestAuthenticationSuccessHandler;
import com.cosmos.origin.jwt.utils.JwtTokenHelper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * JWT Servlet 安全自动配置类
 * <p>
 * 仅在 Servlet Web 环境下加载（如 origin-web）
 * 在 WebFlux 环境下（如 origin-gateway）不加载
 *
 * @author 一陌千尘
 * @date 2025/02/16
 */
@AutoConfiguration(after = JwtAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
public class JwtServletSecurityAutoConfiguration {

    /**
     * 登录成功处理器
     */
    @Bean
    public RestAuthenticationSuccessHandler restAuthenticationSuccessHandler(JwtTokenHelper jwtTokenHelper) {
        return new RestAuthenticationSuccessHandler(jwtTokenHelper);
    }

    /**
     * 登录失败处理器
     */
    @Bean
    public RestAuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }

    /**
     * 登录认证安全配置
     */
    @Bean
    public JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig(
            RestAuthenticationSuccessHandler successHandler,
            RestAuthenticationFailureHandler failureHandler,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        return new JwtAuthenticationSecurityConfig(successHandler, failureHandler, passwordEncoder, userDetailsService);
    }

    /**
     * Token 认证过滤器
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

}
