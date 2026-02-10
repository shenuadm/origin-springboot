package com.cosmos.origin.web.config;

import com.cosmos.origin.admin.domain.dos.UserDO;
import com.cosmos.origin.admin.domain.mapper.UserMapper;
import com.cosmos.origin.admin.enums.LoginStatusEnum;
import com.cosmos.origin.admin.enums.RoleTypeEnum;
import com.cosmos.origin.admin.model.vo.session.UserSessionVO;
import com.cosmos.origin.admin.service.LoginAttemptService;
import com.cosmos.origin.admin.service.LoginLogService;
import com.cosmos.origin.admin.service.UserSessionService;
import com.cosmos.origin.admin.utils.IpLocationUtil;
import com.cosmos.origin.jwt.config.JwtAuthenticationSecurityConfig;
import com.cosmos.origin.jwt.filter.RateLimitFilter;
import com.cosmos.origin.jwt.filter.TokenAuthenticationFilter;
import com.cosmos.origin.jwt.handler.RestAccessDeniedHandler;
import com.cosmos.origin.jwt.handler.RestAuthenticationEntryPoint;
import com.cosmos.origin.jwt.handler.RestAuthenticationSuccessHandler;
import com.cosmos.origin.jwt.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;
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

    // 登录日志和限流服务（可选）
    private final LoginLogService loginLogService;
    private final LoginAttemptService loginAttemptService;
    private final UserSessionService userSessionService;
    private final UserMapper userMapper;

    /**
     * 注入并配置登录成功处理器的会话保存回调
     */
    @Autowired
    public void configureSuccessHandler(RestAuthenticationSuccessHandler successHandler) {
        successHandler.setSessionSaveCallback((request, token) -> {
            try {
                String username = (String) request.getAttribute("LOGIN_USERNAME");
                Long expireMinutes = (Long) request.getAttribute("TOKEN_EXPIRE_MINUTES");
                Boolean rememberMe = (Boolean) request.getAttribute("REMEMBER_ME");
                String rolesStr = (String) request.getAttribute("USER_ROLES");

                log.debug("准备保存会话 - 用户名: {}, 角色字符串: {}", username, rolesStr);

                // 查询用户信息
                UserDO userDO = userMapper.findByUsername(username);
                if (userDO == null) {
                    log.warn("用户 [{}] 不存在，无法保存会话", username);
                    return;
                }

                // 解析角色列表
                java.util.List<String> roles = rolesStr != null && !rolesStr.isEmpty()
                        ? java.util.Arrays.asList(rolesStr.split(","))
                        : java.util.Collections.emptyList();

                log.debug("解析后的角色列表: {}", roles);

                // 构建会话信息
                String ip = RequestUtil.getClientIp(request);
                UserSessionVO sessionVO = UserSessionVO.builder()
                        .username(username)
                        .userId(userDO.getId())
                        .nickname(userDO.getNickname())
                        .roles(roles)
                        .token(token)
                        .loginTime(LocalDateTime.now())
                        .loginIp(ip)
                        .loginLocation(IpLocationUtil.getLocation(ip))
                        .browser(RequestUtil.getBrowser(request))
                        .os(RequestUtil.getOperatingSystem(request))
                        .rememberMe(Boolean.TRUE.equals(rememberMe))
                        .expireTime(LocalDateTime.now().plusMinutes(expireMinutes))
                        .build();

                // 保存会话到 Redis
                userSessionService.saveSession(sessionVO, expireMinutes);
                log.debug("会话保存完成 - 用户: {}, 角色: {}", username, roles);
            } catch (Exception e) {
                log.error("保存用户会话失败", e);
            }
        });
    }

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

                    // 2. 设置账号锁定检查函数（在密码验证前执行）
                    customizer.setLockCheckFunction(username -> {
                        if (loginAttemptService != null) {
                            loginAttemptService.checkLocked(username);
                        }
                        return null;
                    });

                    // 3. 设置登录成功回调（记录日志）
                    customizer.setOnLoginSuccess((request, authentication) -> {
                        String username = authentication.getName();
                        log.debug("用户 [{}] 登录成功", username);

                        // 保存角色信息到请求属性，供会话保存使用
                        String roles = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .reduce((a, b) -> a + "," + b)
                                .orElse("");
                        request.setAttribute("USER_ROLES", roles);

                        if (loginLogService != null) {
                            loginLogService.recordLoginLog(username, LoginStatusEnum.SUCCESS, "登录成功", request);
                        }
                        if (loginAttemptService != null) {
                            loginAttemptService.loginSuccess(username);
                        }
                    });

                    // 4. 设置登录失败回调（记录日志和限流）
                    customizer.setOnLoginFailure((request, exception) -> {
                        String username = getUsernameFromRequest(request);
                        log.debug("用户 [{}] 登录失败: {}", username, exception.getMessage());

                        // 检查是否为锁定异常，如果是锁定异常则不增加失败次数
                        boolean isLockedExceptionType = exception instanceof org.springframework.security.authentication.LockedException;

                        // 如果登录次数限制功能开启，记录失败次数和尝试信息
                        if (loginAttemptService != null && loginAttemptService.isEnabled()) {
                            // 记录失败次数（只有非锁定状态下的失败才增加计数）
                            if (!isLockedExceptionType) {
                                loginAttemptService.loginFailed(username);
                            }

                            // 获取尝试信息并保存到 request，供失败处理器使用
                            Map<String, Object> attemptInfoMap = loginAttemptService.getAttemptInfoAfterFailure(username);
                            request.setAttribute("LOGIN_ATTEMPT_INFO_MAP", attemptInfoMap);
                        }

                        // 记录失败日志
                        if (loginLogService != null) {
                            // 判断账号是否已被锁定（只有功能开启时才检查）
                            boolean isLocked = loginAttemptService != null && loginAttemptService.isEnabled() && loginAttemptService.isLocked(username);

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

                .authorizeHttpRequests(authorize -> {
                    // 放开登录相关接口
                    authorize.requestMatchers("/login", "/logout", "/test").permitAll();
                    // Knife4j 接口文档
                    authorize.requestMatchers("/doc.html", "/v3/api-docs/**", "/favicon.ico", "/webjars/**", "/.well-known/**").permitAll();
                    // websocket 接口
                    authorize.requestMatchers("/ws/**").permitAll();
                    // 管理后台接口需要系统管理员权限
                    authorize.requestMatchers("/manage/user/**").hasAuthority(RoleTypeEnum.SYSTEM_ADMIN.getRoleKey());
                    authorize.requestMatchers("/manage/role/**").hasAuthority(RoleTypeEnum.SYSTEM_ADMIN.getRoleKey());

                    authorize.anyRequest().authenticated();
                })
                // 前后端分离，无需创建会话
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                // 添加 Token 校验过滤器
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置异常处理
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
