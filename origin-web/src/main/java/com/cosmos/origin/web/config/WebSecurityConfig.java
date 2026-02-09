package com.cosmos.origin.web.config;

import com.cosmos.origin.admin.enums.LoginStatusEnum;
import com.cosmos.origin.admin.enums.RoleTypeEnum;
import com.cosmos.origin.admin.service.LoginAttemptService;
import com.cosmos.origin.admin.service.LoginLogService;
import com.cosmos.origin.jwt.config.JwtAuthenticationSecurityConfig;
import com.cosmos.origin.jwt.filter.RateLimitFilter;
import com.cosmos.origin.jwt.filter.TokenAuthenticationFilter;
import com.cosmos.origin.jwt.handler.RestAccessDeniedHandler;
import com.cosmos.origin.jwt.handler.RestAuthenticationEntryPoint;
import com.cosmos.origin.web.filter.LoginAttemptCheckFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

/**
 * Spring Security 配置类
 * <p>
 * 演示了如何使用 .with(config, customizer) 进行多种自定义配置
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationSecurityConfig jwtAuthenticationSecurityConfig;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAccessDeniedHandler deniedHandler;
    private final UserDetailsService userDetailsService;

    // 登录日志和限流服务（可选）
    private final LoginLogService loginLogService;
    private final LoginAttemptService loginAttemptService;

    // 登录检查过滤器（可选）
    private final LoginAttemptCheckFilter loginAttemptCheckFilter;

    /**
     * 核心配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           TokenAuthenticationFilter tokenAuthenticationFilter,
                                           RateLimitFilter rateLimitFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // ============================================
                // 登录认证配置（使用 .with 自定义）
                // ============================================
                .with(jwtAuthenticationSecurityConfig, customizer -> {
                    // 1. 修改登录 URL 和参数名（可选）
                    // customizer.setLoginProcessingUrl("/api/auth/login");
                    // customizer.setUsernameParameter("email");
                    // customizer.setPasswordParameter("passwd");

                    // 2. 设置登录成功回调（记录日志）
                    customizer.setOnLoginSuccess((request, authentication) -> {
                        String username = authentication.getName();
                        log.debug("用户 [{}] 登录成功", username);

                        if (loginLogService != null) {
                            loginLogService.recordLoginLog(username, LoginStatusEnum.SUCCESS, "登录成功", request);
                        }
                        if (loginAttemptService != null) {
                            loginAttemptService.loginSuccess(username);
                        }
                    });

                    // 3. 设置登录失败回调（记录日志和限流）
                    customizer.setOnLoginFailure((request, exception) -> {
                        String username = getUsernameFromRequest(request);
                        log.debug("用户 [{}] 登录失败: {}", username, exception.getMessage());

                        // 记录失败次数（在记录日志之前，先记录失败）
                        if (loginAttemptService != null) {
                            loginAttemptService.loginFailed(username);

                            // 获取尝试信息并保存到 request，供失败处理器使用
                            Map<String, Object> attemptInfoMap = loginAttemptService.getAttemptInfoAfterFailure(username);
                            request.setAttribute("LOGIN_ATTEMPT_INFO_MAP", attemptInfoMap);
                        }

                        // 记录失败日志，根据账号是否被锁定记录正确的状态
                        if (loginLogService != null) {
                            // 判断账号是否已被锁定
                            boolean isLocked = loginAttemptService != null && loginAttemptService.isLocked(username);
                            
                            LoginStatusEnum status;
                            String message;
                            
                            if (isLocked) {
                                // 账号已被锁定
                                status = LoginStatusEnum.LOCKED;
                                long lockRemainingMinutes = loginAttemptService.getLockRemainingMinutes(username);
                                message = String.format("登录失败次数过多，账号已被锁定，请 %d 分钟后重试", lockRemainingMinutes);
                            } else {
                                // 登录失败但未锁定
                                status = LoginStatusEnum.FAILED;
                                message = exception instanceof org.springframework.security.authentication.BadCredentialsException
                                        ? "用户名或密码错误" : exception.getMessage();
                            }
                            
                            loginLogService.recordLoginLog(username, status, message, request);
                        }
                    });
                })

                // 配置记住我功能
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("rememberMe")
                )
                .authorizeHttpRequests(authorize -> {
                    // 放开登录相关接口
                    authorize.requestMatchers("/login", "/logout", "/test").permitAll();
                    // Knife4j 接口文档
                    authorize.requestMatchers("/doc.html", "/v3/api-docs/**", "/favicon.ico", "/webjars/**", "/.well-known/**").permitAll();
                    // 管理后台接口需要系统管理员权限
                    authorize.requestMatchers("/manage/user/**").hasAuthority(RoleTypeEnum.SYSTEM_ADMIN.getRoleKey());
                    authorize.requestMatchers("/manage/role/**").hasAuthority(RoleTypeEnum.SYSTEM_ADMIN.getRoleKey());

                    authorize.anyRequest().authenticated();
                })
                // 错误处理
                .exceptionHandling(m -> {
                    m.authenticationEntryPoint(authEntryPoint);
                    m.accessDeniedHandler(deniedHandler);
                })
                // 前后端分离，无需创建会话
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                // 添加登录尝试检查过滤器（在认证过滤器之前）
                .addFilterBefore(loginAttemptCheckFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加 Token 校验过滤器
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置异常处理，处理 LoginAttemptExceededException
                .exceptionHandling(m -> {
                    m.authenticationEntryPoint(authEntryPoint);
                    m.accessDeniedHandler(deniedHandler);
                });

        return http.build();
    }

    /**
     * 从请求中获取用户名
     */
    private String getUsernameFromRequest(HttpServletRequest request) {
        String username = (String) request.getAttribute("LOGIN_USERNAME");
        return username != null ? username : "unknown";
    }

    /**
     * Token 校验过滤器
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    /**
     * 限流过滤器
     */
    @Bean
    public RateLimitFilter rateLimitFilter() {
        return new RateLimitFilter();
    }
}
