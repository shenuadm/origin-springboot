package com.cosmos.origin.auth.constant;

public class RedisKeyConstants {

    /**
     * 验证码 KEY 前缀
     */
    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";

    /**
     * 用户角色数据 KEY 前缀
     */
    private static final String USER_ROLES_KEY_PREFIX = "user:roles:";


    /**
     * 构建用户-角色 Key
     *
     * @param username 用户名
     * @return 用户-角色 Key
     */
    public static String buildUserRoleKey(String username) {
        return USER_ROLES_KEY_PREFIX + username;
    }

    /**
     * 构建验证码 KEY
     *
     * @param phone 手机号
     * @return 验证码 KEY
     */
    public static String buildVerificationCodeKey(String phone) {
        return VERIFICATION_CODE_KEY_PREFIX + phone;
    }

    /**
     * 角色对应的权限集合 KEY 前缀
     */
    private static final String ROLE_PERMISSIONS_KEY_PREFIX = "role:permissions:";


    /**
     * 构建角色对应的权限集合 KEY
     *
     * @param roleId 角色 ID
     * @return 角色对应的权限集合 KEY
     */
    public static String buildRolePermissionsKey(Long roleId) {
        return ROLE_PERMISSIONS_KEY_PREFIX + roleId;
    }
}
