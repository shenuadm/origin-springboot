package com.cosmos.origin.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Disabled("需要 Redis 服务才能运行")
@Slf4j
public class RedisTests {

    /**
     * set key value
     */
    @Test
    void testSetKeyValue() {
        // 需要 Redis 服务才能运行
    }

    /**
     * 判断某个 key 是否存在
     */
    @Test
    void testHasKey() {
        // 需要 Redis 服务才能运行
    }

    /**
     * 获取某个 key 的 value
     */
    @Test
    void testGetValue() {
        // 需要 Redis 服务才能运行
    }

    /**
     * 删除某个 key
     */
    @Test
    void testDelete() {
        // 需要 Redis 服务才能运行
    }
}
