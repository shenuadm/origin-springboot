# 测试指南

## 测试目录结构

```
src/test/java
├── origin-admin/
│   ├── origin-admin-api/
│   │   └── AdminConstantsTests.java
│   │   └── AdminEnumsTests.java
│   └── origin-admin-biz/
│       └── OriginAdminApplicationTests.java
│
├── origin-auth/
│   ├── OriginModuleAuthApplicationTests.java
│   └── ThreadPoolTaskExecutorTests.java
│
├── origin-comment/
│   └── origin-comment-biz/
│       └── OriginCommentApplicationTests.java
│
├── origin-example/
│   └── RedisTests.java
│
└── origin-framework/
    └── origin-common/
        ├── ResponseTests.java
        ├── JsonUtilTests.java
        └── ExceptionTests.java
```

## 运行测试

### 运行所有测试

```bash
mvn test
```

### 运行特定模块测试

```bash
# 运行 admin 模块测试
mvn test -pl origin-admin

# 运行 common 模块测试
mvn test -pl origin-framework/origin-common
```

### 运行单个测试类

```bash
mvn test -Dtest=ResponseTests
```

### 运行单个测试方法

```bash
mvn test -Dtest=ResponseTests#testSuccessResponseWithData
```

## 测试规范

### 单元测试规范

1. **测试类命名**: `{ClassName}Tests`
2. **测试方法命名**: `test{methodName}_{scenario}`
3. **每个测试方法应独立**: 不依赖其他测试的执行顺序
4. **使用断言**: 优先使用 JUnit 5 的断言方法

### 示例

```java
public class UserServiceTests {

    @Test
    void testFindUserById() {
        // Arrange - 准备测试数据
        Long userId = 1L;

        // Act - 执行被测试的方法
        User user = userService.findById(userId);

        // Assert - 验证结果
        assertNotNull(user);
        assertEquals(userId, user.getId());
    }

    @Test
    void testFindUserById_NotFound() {
        // Arrange
        Long userId = 999L;

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.findById(userId);
        });
    }
}
```

### Spring Boot 测试

对于需要 Spring 上下文的测试，使用 `@SpringBootTest`：

```java
@SpringBootTest
@Slf4j
class RedisTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testSetKeyValue() {
        redisTemplate.opsForValue().set("testKey", "testValue");
        assertEquals("testValue", redisTemplate.opsForValue().get("testKey"));
    }
}
```

### 排除自动配置

对于不需要数据库的测试，可以排除数据源自动配置：

```java
@SpringBootTest
@Slf4j
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    CommonAutoConfiguration.class,
})
class SomeServiceTests {
    // ...
}
```

## 测试覆盖

### 当前测试覆盖

| 模块 | 测试类型 | 说明 |
|------|----------|------|
| origin-common | 单元测试 | Response, JsonUtil, Exception |
| origin-admin-api | 单元测试 | AdminConstants, Enums |
| origin-auth | 功能测试 | 线程池测试 |
| origin-example | 集成测试 | Redis 操作 |
| origin-admin-biz | 上下文测试 | Spring 上下文加载 |
| origin-comment-biz | 上下文测试 | Spring 上下文加载 |
| origin-web | 上下文测试 | Spring 上下文加载 |

### 添加新测试

1. 在对应的 `src/test/java` 目录下创建测试类
2. 确保测试类包名与主代码一致
3. 继承适当的测试基类或使用注解
4. 运行 `mvn test` 验证测试通过

## 测试工具类

项目中使用的主要测试依赖：

- JUnit 5 (junit-jupiter)
- Spring Boot Test
- AssertJ
- Mockito
