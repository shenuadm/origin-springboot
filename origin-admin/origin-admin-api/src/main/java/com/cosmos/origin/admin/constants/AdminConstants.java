package com.cosmos.origin.admin.constants;

/**
 * 管理模块常量类
 * <p>
 * 定义用户管理、角色管理、权限管理等模块的常量
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public final class AdminConstants {

    private AdminConstants() {
    }

    // ==================== 用户相关常量 ====================

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 用户名最小长度
     */
    public static final int USERNAME_MIN_LENGTH = 4;

    /**
     * 用户名最大长度
     */
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 昵称最大长度
     */
    public static final int NICKNAME_MAX_LENGTH = 50;

    /**
     * 手机号正则表达式
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 邮箱正则表达式
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // ==================== 角色相关常量 ====================

    /**
     * 角色名最大长度
     */
    public static final int ROLE_NAME_MAX_LENGTH = 50;

    /**
     * 角色标识最大长度
     */
    public static final int ROLE_CODE_MAX_LENGTH = 50;

    /**
     * 角色描述最大长度
     */
    public static final int ROLE_DESCRIPTION_MAX_LENGTH = 200;

    /**
     * 超级管理员角色标识
     */
    public static final String SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN";

    /**
     * 普通管理员角色标识
     */
    public static final String ADMIN_ROLE_CODE = "ADMIN";

    /**
     * 普通用户角色标识
     */
    public static final String USER_ROLE_CODE = "USER";

    // ==================== 权限相关常量 ====================

    /**
     * 权限名最大长度
     */
    public static final int PERMISSION_NAME_MAX_LENGTH = 50;

    /**
     * 权限标识最大长度
     */
    public static final int PERMISSION_CODE_MAX_LENGTH = 100;

    // ==================== 分页相关常量 ====================

    /**
     * 默认每页数量
     */
    public static final long DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页数量
     */
    public static final long MAX_PAGE_SIZE = 100;

    // ==================== 登录相关常量 ====================

    /**
     * 默认登录会话过期时间（分钟）
     */
    public static final long DEFAULT_SESSION_EXPIRE_MINUTES = 120;

    /**
     * 单设备登录策略
     */
    public static final String LOGIN_STRATEGY_SINGLE = "single";

    /**
     * 多设备登录策略
     */
    public static final String LOGIN_STRATEGY_MULTIPLE = "multiple";

    /**
     * 默认登录策略
     */
    public static final String DEFAULT_LOGIN_STRATEGY = LOGIN_STRATEGY_MULTIPLE;
}
