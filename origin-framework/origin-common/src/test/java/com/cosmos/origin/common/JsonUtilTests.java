package com.cosmos.origin.common;

import com.cosmos.origin.common.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON工具类测试
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
public class JsonUtilTests {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestUser {
        private String name;
        private Integer age;
        private String email;
    }

    @Test
    void testToJsonStringWithSimpleObject() {
        TestUser user = new TestUser("张三", 25, "zhangsan@example.com");

        String json = JsonUtil.toJsonString(user);

        assertNotNull(json);
        assertTrue(json.contains("张三"));
        assertTrue(json.contains("25"));
        assertTrue(json.contains("zhangsan@example.com"));
    }

    @Test
    void testToJsonStringWithNullObject() {
        String json = JsonUtil.toJsonString(null);

        assertEquals("null", json);
    }

    @Test
    void testParseObject() {
        String json = "{\"name\":\"李四\",\"age\":30,\"email\":\"lisi@example.com\"}";

        TestUser user = JsonUtil.parseObject(json, TestUser.class);

        assertNotNull(user);
        assertEquals("李四", user.getName());
        assertEquals(Integer.valueOf(30), user.getAge());
        assertEquals("lisi@example.com", user.getEmail());
    }

    @Test
    void testParseObjectWithNullJson() {
        assertThrows(Exception.class, () -> {
            JsonUtil.parseObject(null, TestUser.class);
        });
    }

    @Test
    void testParseObjectWithInvalidJson() {
        assertThrows(Exception.class, () -> {
            JsonUtil.parseObject("invalid json", TestUser.class);
        });
    }

    @Test
    void testRoundTrip() {
        TestUser originalUser = new TestUser("王五", 28, "wangwu@example.com");

        String json = JsonUtil.toJsonString(originalUser);
        TestUser parsedUser = JsonUtil.parseObject(json, TestUser.class);

        assertEquals(originalUser.getName(), parsedUser.getName());
        assertEquals(originalUser.getAge(), parsedUser.getAge());
        assertEquals(originalUser.getEmail(), parsedUser.getEmail());
    }

    @Test
    void testParseObjectWithPartialFields() {
        String json = "{\"name\":\"赵六\",\"age\":35}";

        TestUser user = JsonUtil.parseObject(json, TestUser.class);

        assertNotNull(user);
        assertEquals("赵六", user.getName());
        assertEquals(Integer.valueOf(35), user.getAge());
        assertNull(user.getEmail()); // 未提供的字段应为null
    }
}
