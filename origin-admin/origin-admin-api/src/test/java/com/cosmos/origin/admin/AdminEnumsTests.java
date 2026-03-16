package com.cosmos.origin.admin;

import com.cosmos.origin.admin.enums.LoginStatusEnum;
import com.cosmos.origin.admin.enums.RoleTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Admin模块枚举测试
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public class AdminEnumsTests {

    @Test
    void testLoginStatusEnum() {
        assertEquals(3, LoginStatusEnum.values().length);

        assertEquals(Integer.valueOf(1), LoginStatusEnum.SUCCESS.getCode());
        assertEquals("登录成功", LoginStatusEnum.SUCCESS.getDescription());

        assertEquals(Integer.valueOf(0), LoginStatusEnum.FAILED.getCode());
        assertEquals("登录失败", LoginStatusEnum.FAILED.getDescription());

        assertEquals(Integer.valueOf(-1), LoginStatusEnum.LOCKED.getCode());
        assertEquals("账号被锁定", LoginStatusEnum.LOCKED.getDescription());

        assertEquals(LoginStatusEnum.SUCCESS, LoginStatusEnum.valueOf("SUCCESS"));
        assertEquals(LoginStatusEnum.FAILED, LoginStatusEnum.valueOf("FAILED"));
        assertEquals(LoginStatusEnum.LOCKED, LoginStatusEnum.valueOf("LOCKED"));
    }

    @Test
    void testRoleTypeEnum() {
        assertNotNull(RoleTypeEnum.values());
        assertEquals(1, RoleTypeEnum.values().length);

        RoleTypeEnum systemAdmin = RoleTypeEnum.SYSTEM_ADMIN;
        assertNotNull(systemAdmin);
        assertEquals("ROLE_SYSTEM_ADMIN", systemAdmin.getRoleKey());
        assertEquals("系统管理员", systemAdmin.getRoleName());
    }
}
