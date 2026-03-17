package com.cosmos.origin.admin;

import com.cosmos.origin.admin.constants.AdminConstants;
import com.cosmos.origin.admin.enums.AdminExceptionEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Admin模块常量与枚举测试
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public class AdminConstantsTests {

    // ==================== 常量测试 ====================

    @Test
    void testDefaultPassword() {
        assertEquals("123456", AdminConstants.DEFAULT_PASSWORD);
    }

    @Test
    void testUsernameLengthConstraints() {
        assertEquals(4, AdminConstants.USERNAME_MIN_LENGTH);
        assertEquals(20, AdminConstants.USERNAME_MAX_LENGTH);
    }

    @Test
    void testPasswordLengthConstraints() {
        assertEquals(6, AdminConstants.PASSWORD_MIN_LENGTH);
        assertEquals(20, AdminConstants.PASSWORD_MAX_LENGTH);
    }

    @Test
    void testNicknameMaxLength() {
        assertEquals(50, AdminConstants.NICKNAME_MAX_LENGTH);
    }

    @Test
    void testPhoneRegex() {
        assertNotNull(AdminConstants.PHONE_REGEX);
        assertTrue(AdminConstants.PHONE_REGEX.startsWith("^1"));
    }

    @Test
    void testEmailRegex() {
        assertNotNull(AdminConstants.EMAIL_REGEX);
        assertTrue(AdminConstants.EMAIL_REGEX.contains("@"));
    }

    @Test
    void testRoleConstraints() {
        assertEquals(50, AdminConstants.ROLE_NAME_MAX_LENGTH);
        assertEquals(50, AdminConstants.ROLE_CODE_MAX_LENGTH);
        assertEquals(200, AdminConstants.ROLE_DESCRIPTION_MAX_LENGTH);
    }

    @Test
    void testRoleCodes() {
        assertEquals("SUPER_ADMIN", AdminConstants.SUPER_ADMIN_ROLE_CODE);
        assertEquals("ADMIN", AdminConstants.ADMIN_ROLE_CODE);
        assertEquals("USER", AdminConstants.USER_ROLE_CODE);
    }

    @Test
    void testPermissionConstraints() {
        assertEquals(50, AdminConstants.PERMISSION_NAME_MAX_LENGTH);
        assertEquals(100, AdminConstants.PERMISSION_CODE_MAX_LENGTH);
    }

    @Test
    void testPaginationConstraints() {
        assertEquals(10, AdminConstants.DEFAULT_PAGE_SIZE);
        assertEquals(100, AdminConstants.MAX_PAGE_SIZE);
    }

    @Test
    void testSessionConstraints() {
        assertEquals(120, AdminConstants.DEFAULT_SESSION_EXPIRE_MINUTES);
        assertEquals("single", AdminConstants.LOGIN_STRATEGY_SINGLE);
        assertEquals("multiple", AdminConstants.LOGIN_STRATEGY_MULTIPLE);
        assertEquals("multiple", AdminConstants.DEFAULT_LOGIN_STRATEGY);
    }

    // ==================== 异常枚举测试 ====================

    @Test
    void testUserExceptionEnums() {
        assertEquals("30000", AdminExceptionEnum.USER_NOT_FOUND.getErrorCode());
        assertEquals("用户不存在", AdminExceptionEnum.USER_NOT_FOUND.getErrorMessage());

        assertEquals("30001", AdminExceptionEnum.USER_ALREADY_EXISTS.getErrorCode());
        assertEquals("用户已存在", AdminExceptionEnum.USER_ALREADY_EXISTS.getErrorMessage());

        assertEquals("30002", AdminExceptionEnum.USER_DISABLED.getErrorCode());
        assertEquals("用户已被禁用", AdminExceptionEnum.USER_DISABLED.getErrorMessage());

        assertEquals("30003", AdminExceptionEnum.USER_PASSWORD_ERROR.getErrorCode());
        assertEquals("密码错误", AdminExceptionEnum.USER_PASSWORD_ERROR.getErrorMessage());
    }

    @Test
    void testRoleExceptionEnums() {
        assertEquals("30010", AdminExceptionEnum.ROLE_NOT_FOUND.getErrorCode());
        assertEquals("角色不存在", AdminExceptionEnum.ROLE_NOT_FOUND.getErrorMessage());

        assertEquals("30011", AdminExceptionEnum.ROLE_ALREADY_EXISTS.getErrorCode());
        assertEquals("角色已存在", AdminExceptionEnum.ROLE_ALREADY_EXISTS.getErrorMessage());

        assertEquals("30012", AdminExceptionEnum.ROLE_CANNOT_DELETE.getErrorCode());
        assertEquals("该角色下存在用户，无法删除", AdminExceptionEnum.ROLE_CANNOT_DELETE.getErrorMessage());

        assertEquals("30015", AdminExceptionEnum.ROLE_IS_SYSTEM_ROLE.getErrorCode());
        assertEquals("系统角色不可操作", AdminExceptionEnum.ROLE_IS_SYSTEM_ROLE.getErrorMessage());
    }

    @Test
    void testPermissionExceptionEnums() {
        assertEquals("30020", AdminExceptionEnum.PERMISSION_NOT_FOUND.getErrorCode());
        assertEquals("权限不存在", AdminExceptionEnum.PERMISSION_NOT_FOUND.getErrorMessage());

        assertEquals("30021", AdminExceptionEnum.PERMISSION_ALREADY_EXISTS.getErrorCode());
        assertEquals("权限已存在", AdminExceptionEnum.PERMISSION_ALREADY_EXISTS.getErrorMessage());
    }

    @Test
    void testSessionExceptionEnums() {
        assertEquals("30030", AdminExceptionEnum.SESSION_NOT_FOUND.getErrorCode());
        assertEquals("会话不存在", AdminExceptionEnum.SESSION_NOT_FOUND.getErrorMessage());

        assertEquals("30031", AdminExceptionEnum.SESSION_EXPIRED.getErrorCode());
        assertEquals("会话已过期", AdminExceptionEnum.SESSION_EXPIRED.getErrorMessage());
    }

    @Test
    void testExceptionEnumCount() {
        // 验证异常码数量
        AdminExceptionEnum[] values = AdminExceptionEnum.values();
        assertTrue(values.length > 0);
    }
}
