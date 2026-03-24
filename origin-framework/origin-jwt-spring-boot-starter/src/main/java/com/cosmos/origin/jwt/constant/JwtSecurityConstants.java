
package com.cosmos.origin.jwt.constant;

import lombok.NoArgsConstructor;

/**
 * JWT 安全模块常量类
 * <p>
 * 统一管理登录、认证等相关常量，避免硬编码分散在多处
 *
 * @author 一陌千尘
 * @date 2026/02/13
 */
@NoArgsConstructor // 禁止实例化
public final class JwtSecurityConstants {

    /**
     * 登录处理URL，默认 /login
     */
    public static final String DEFAULT_LOGIN_URL = "/login";

    /**
     * 用户名字段名，默认 username
     */
    public static final String USERNAME_PARAMETER = "username";

    /**
     * 密码字段名，默认 password
     */
    public static final String PASSWORD_PARAMETER = "password";

    /**
     * 记住我字段名，默认 rememberMe
     */
    public static final String REMEMBER_ME_PARAMETER = "rememberMe";

    /**
     * 请求属性中存储用户名的 key
     */
    public static final String LOGIN_USERNAME_ATTRIBUTE = "LOGIN_USERNAME";

    /**
     * 请求属性中存储 token 过期时间的 key
     */
    public static final String TOKEN_EXPIRE_MINUTES_ATTRIBUTE = "TOKEN_EXPIRE_MINUTES";

    /**
     * 请求属性中存储记住我状态的 key
     */
    public static final String REMEMBER_ME_ATTRIBUTE = "REMEMBER_ME";

    /**
     * 请求属性中存储用户角色的 key
     */
    public static final String USER_ROLES_ATTRIBUTE = "USER_ROLES";

    /**
     * 请求属性中存储登录尝试信息的 key
     */
    public static final String LOGIN_ATTEMPT_INFO_MAP_ATTRIBUTE = "LOGIN_ATTEMPT_INFO_MAP";

    /**
     * Refresh Token 请求参数名
     */
    public static final String REFRESH_TOKEN_PARAMETER = "refreshToken";

    /**
     * Refresh Token 刷新 URL
     */
    public static final String REFRESH_TOKEN_URL = "/auth/refreshToken";
}
