package com.cosmos.origin.admin.config;

import com.cosmos.origin.common.enums.RoleTypeEnum;
import com.cosmos.origin.jwt.config.JwtAuthenticationSecurityConfig;
import com.cosmos.origin.jwt.filter.TokenAuthenticationFilter;
import com.cosmos.origin.jwt.handler.RestAccessDeniedHandler;
import com.cosmos.origin.jwt.handler.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 * <p>
 * EnableMethodSecurity 注解表示启用 @PreAuthorize 和 @PostAuthorize 注解，securedEnabled = true 表示启用 @Secured 注解
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
@Slf4j
@Configuration
@EnableWebSecurity
// @EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAccessDeniedHandler deniedHandler;

    /**
     * 核心配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // 禁用 csrf
                .formLogin(AbstractHttpConfigurer::disable) // 禁用表单登录
                // 设置用户登录认证相关配置
                .with(jwtAuthenticationSecurityConfig, customizer -> {
                    // 这里可以添加自定义配置（如果需要）
                })
                .authorizeHttpRequests(authorize -> { // 处理请求
                    // 放开哪些接口
                    authorize.requestMatchers("/login", "/logout", "/test").permitAll(); // 登录接口，登出接口，测试接口
                    authorize.requestMatchers("/doc.html", "/v3/api-docs/**", "/favicon.ico", "/webjars/**", "/.well-known/**").permitAll(); // knife4j 接口文档
                    // 用户管理相关接口
                    authorize.requestMatchers("/manage/user/**").hasAuthority(RoleTypeEnum.SYSTEM_ADMIN.getRoleKey());
                    // 角色管理相关接口
                    authorize.requestMatchers("/manage/role/**").hasAuthority(RoleTypeEnum.SYSTEM_ADMIN.getRoleKey());

                    authorize.anyRequest().authenticated();
                })
                // 错误处理
                .exceptionHandling(m -> {
                    m.authenticationEntryPoint(authEntryPoint); // 认证失败处理 401，处理用户未登录访问受保护的资源的情况
                    m.accessDeniedHandler(deniedHandler); // 拒绝访问处理 403，处理登录成功后访问受保护的资源，但是权限不够的情况
                })
                // 前后端分离，无需创建会话
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                // 将 Token 校验过滤器添加到用户认证过滤器之前，如果使用token这个配置是必须的
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Token 校验过滤器
     *
     * @return Token 校验过滤器
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
}
