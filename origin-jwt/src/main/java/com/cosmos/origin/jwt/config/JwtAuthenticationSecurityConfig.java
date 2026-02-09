package com.cosmos.origin.jwt.config;

import com.cosmos.origin.jwt.filter.JwtAuthenticationFilter;
import com.cosmos.origin.jwt.handler.RestAuthenticationFailureHandler;
import com.cosmos.origin.jwt.handler.RestAuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 自定义登录过滤器配置类
 * <p>
 * 支持通过 .with(config, customizer) 进行自定义配置：
 * - 自定义登录URL
 * - 自定义请求参数名
 * - 登录成功/失败回调（可用于记录日志、限流等）
 *
 * @author 一陌千尘
 * @date 2025/11/04
 */
@Slf4j
@Configuration
public class JwtAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    /**
     * -- GETTER --
     * 获取默认成功处理器
     */
    @Getter
    private final RestAuthenticationSuccessHandler defaultSuccessHandler;
    /**
     * -- GETTER --
     * 获取默认失败处理器
     */
    @Getter
    private final RestAuthenticationFailureHandler defaultFailureHandler;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    // ========== 可配置属性（通过 customizer 设置） ==========

    /**
     * 登录处理URL，默认 /login
     */
    @Setter
    @Getter
    private String loginProcessingUrl = "/login";

    /**
     * 用户名字段名，默认 username
     */
    @Setter
    private String usernameParameter = "username";

    /**
     * 密码字段名，默认 password
     */
    @Setter
    private String passwordParameter = "password";

    /**
     * 自定义成功处理器（覆盖默认的）
     */
    @Setter
    private AuthenticationSuccessHandler customSuccessHandler;

    /**
     * 自定义失败处理器（覆盖默认的）
     */
    @Setter
    private AuthenticationFailureHandler customFailureHandler;

    /**
     * 登录成功回调（在默认处理器之后执行）
     * 参数：(request, authentication)
     */
    @Setter
    private BiConsumer<HttpServletRequest, Authentication> onLoginSuccess;

    /**
     * 登录失败回调（在默认处理器之后执行）
     * 参数：(request, exception)
     */
    @Setter
    private BiConsumer<HttpServletRequest, AuthenticationException> onLoginFailure;

    /**
     * 账号锁定检查函数（在密码验证前执行）
     * 参数：username
     * 如果账号被锁定，应抛出 LockedException
     */
    @Setter
    private Function<String, Void> lockCheckFunction;

    /**
     * 过滤器实例（配置完成后可获取）
     */
    @Getter
    private JwtAuthenticationFilter filter;

    public JwtAuthenticationSecurityConfig(
            RestAuthenticationSuccessHandler defaultSuccessHandler,
            RestAuthenticationFailureHandler defaultFailureHandler,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        this.defaultSuccessHandler = defaultSuccessHandler;
        this.defaultFailureHandler = defaultFailureHandler;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) {
        // 创建过滤器并设置自定义URL和参数名
        this.filter = new JwtAuthenticationFilter(loginProcessingUrl);
        filter.setUsernameParameter(usernameParameter);
        filter.setPasswordParameter(passwordParameter);
        filter.setAuthenticationManager(httpSecurity.getSharedObject(AuthenticationManager.class));

        // 设置账号锁定检查函数
        if (lockCheckFunction != null) {
            filter.setLockCheckFunction(lockCheckFunction);
        }

        // 设置处理器（包装以支持回调功能）
        AuthenticationSuccessHandler successHandler = createSuccessHandler();
        AuthenticationFailureHandler failureHandler = createFailureHandler();

        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);

        // 配置认证提供者
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        httpSecurity.authenticationProvider(provider);

        // 添加过滤器
        httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        log.debug("JwtAuthenticationFilter 配置完成: url={}, usernameParam={}, passwordParam={}",
                loginProcessingUrl, usernameParameter, passwordParameter);
    }

    /**
     * 创建成功处理器（包装以支持回调）
     */
    private AuthenticationSuccessHandler createSuccessHandler() {
        AuthenticationSuccessHandler delegate = customSuccessHandler != null ? customSuccessHandler : defaultSuccessHandler;

        return (request, response, authentication) -> {
            // 1. 先执行回调（设置 USER_ROLES 等属性），确保 delegate 能获取到这些信息
            if (onLoginSuccess != null) {
                try {
                    onLoginSuccess.accept(request, authentication);
                } catch (Exception e) {
                    log.error("登录成功回调执行失败", e);
                }
            }

            // 2. 再执行原有成功处理逻辑（此时 request 中已有角色等信息）
            delegate.onAuthenticationSuccess(request, response, authentication);
        };
    }

    /**
     * 创建失败处理器（包装以支持回调）
     */
    private AuthenticationFailureHandler createFailureHandler() {
        AuthenticationFailureHandler delegate = customFailureHandler != null ? customFailureHandler : defaultFailureHandler;

        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                // 1. 先执行回调（记录失败次数等），确保 delegate 能获取到最新信息
                if (onLoginFailure != null) {
                    try {
                        onLoginFailure.accept(request, exception);
                    } catch (Exception e) {
                        log.error("登录失败回调执行失败", e);
                    }
                }

                // 2. 执行原有失败处理逻辑（此时回调已设置好尝试信息）
                delegate.onAuthenticationFailure(request, response, exception);
            }
        };
    }

}
