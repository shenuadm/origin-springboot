# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

origin-springboot 是一个现代化的 Spring Boot 技术底座项目，采用 Spring Boot 3.5.10 + Spring Cloud 2025 技术栈构建。项目采用模块化单体 (Modular Monolith) 架构设计，支持单体模式和微服务模式通过 Maven Profile 一键切换。

## Common Commands

```bash
# 单体模式编译打包（默认）
mvn clean package -P monolith -DskipTests

# 微服务模式编译打包
mvn clean package -P microservice -DskipTests

# 运行单体模式应用
java -jar origin-web/target/origin-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 运行微服务模式应用（需先启动 Nacos）
java -jar origin-web/target/origin-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=microservice,dev

# 运行所有测试
mvn test

# 运行特定模块的测试
mvn test -pl origin-admin

# 运行特定测试类
mvn test -Dtest=OriginWebApplicationTests

# 查看依赖树
mvn dependency:tree

# 清理编译产物
mvn clean
```

## Architecture

### Module Structure

```
origin-springboot (父工程)
├── origin-framework (基础框架层，11个 Starter)
│   ├── origin-common                    # 通用工具组件
│   ├── origin-jwt-spring-boot-starter  # JWT认证组件
│   ├── origin-jackson-spring-boot-starter
│   ├── origin-operationlog-spring-boot-starter
│   ├── origin-event-spring-boot-starter
│   ├── origin-scheduler-spring-boot-starter
│   ├── origin-websocket-spring-boot-starter
│   ├── origin-redis-spring-boot-starter
│   ├── origin-oss-spring-boot-starter
│   ├── origin-gateway-spring-boot-starter
│   └── origin-spring-cloud-starter
├── origin-auth           # 认证服务模块
├── origin-admin          # 管理后台聚合模块
│   ├── origin-admin-api  # 接口层（VO、Enums、API）
│   └── origin-admin-biz # 业务实现层
├── origin-comment        # 评论模块聚合
│   ├── origin-comment-api
│   └── origin-comment-biz
├── origin-web           # 单体运行入口（唯一启动模块）
├── origin-gateway        # API 网关服务
└── origin-example        # 示例代码模块
```

### Design Patterns

1. **Starter 化装配**: 基础组件采用 Spring Boot Starter 模式封装，通过 AutoConfiguration 自动装配
2. **接口与实现分离**: 业务模块拆分为 `api` 层和 `biz` 层，单体模式直接依赖，微服务模式可切换为 Feign 远程调用
3. **统一响应结构**: 所有 API 通过 `Response` 类统一封装，分页查询使用 `PageResponse`
4. **全局异常处理**: 通过 `GlobalExceptionHandler` 统一处理 `BizException` 和系统异常
5. **API 日志记录**: 使用 `@ApiOperationLog` 注解 + AOP 记录请求入参、出参和耗时
6. **VO 模式**: 请求使用 ReqVO，响应使用 RspVO，严格区分业务层和展示层

## Code Organization

| 类型 | 位置 | 说明 |
|------|------|------|
| DO (Data Object) | `domain.dos` | 数据库实体对象，继承 `BaseEntity` |
| Mapper | `domain.mapper` | MyBatis Flex 接口 |
| Service | `service` | 业务逻辑层，接口和实现分离 |
| Controller | `controller` | 控制器层 |
| ReqVO | `model.vo.*` | 请求参数对象，需参数校验 |
| RspVO | `model.vo.*` | 响应结果对象 |

### VO 命名规范

- 分页请求: `{业务}PageListReqVO` → `FindUserPageListReqVO`
- 分页响应: `{业务}PageListRspVO`
- 详情请求: `Find{业务}InfoReqVO`
- 添加请求: `Add{业务}ReqVO` → `AddUserReqVO`
- 更新请求: `Update{业务}ReqVO` → `UpdateUserReqVO`
- 删除请求: `Delete{业务}ReqVO`

### Controller 规范

```java
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AdminUserServiceApi userService;

    @GetMapping("/page")
    @ApiOperation("获取用户分页列表")
    @ApiOperationLog(description = "获取用户分页列表")
    public PageResponse<?> findUserPageList(FindUserPageListReqVO reqVO) {
        return userService.findUserPageList(reqVO);
    }
}
```

## Key Technologies

- Java 17 + Spring Boot 3.5.10 + Spring Cloud 2025.0.0
- PostgreSQL 42.7.8 + MyBatis Flex 1.11.5
- Redis + Redisson 3.27.0 (分布式锁)
- JWT (jjwt 0.11.2) + Spring Security
- Sentinel 限流熔断（微服务模式）
- Knife4j 4.6.0 API 文档

## Important Paths

- 应用入口: `origin-web/src/main/java/com/cosmos/origin/web/OriginWebApplication.java`
- 组件扫描: `com.cosmos.origin.*`
- 主配置文件: `origin-web/src/main/resources/application.yml`
- 微服务配置: `origin-web/src/main/resources/application-microservice.yml`
- 数据库初始化: `docs/sql/origin.sql`
- API 文档: `http://localhost:8081/doc.html`

## Built-in Features

### Distributed Lock

```java
@RedissonLock(keys = "#userId")
public void processUser(Long userId) {
    // 业务逻辑
}
```

### Sensitive Word Filter

```java
@Autowired
private SensitiveWordUtil sensitiveWordUtil;

boolean hasSensitive = sensitiveWordUtil.containsSensitive(text);
List<String> sensitiveWords = sensitiveWordUtil.findSensitiveWords(text);
String cleanText = sensitiveWordUtil.replaceSensitive(text, '*');
```

### API Logging

Use `@ApiOperationLog` annotation on Controller methods to automatically log requests:
```java
@ApiOperationLog(description = "创建订单")
public Response<?> createOrder(@Valid @RequestBody CreateOrderReqVO reqVO) { }
```

## Documentation

- `docs/接口与模块开发规范.md` - API 设计规范和模块开发指南
- `docs/单体模式与微服务模式开发指南.md` - 双模式开发指南
- `docs/系统设计指南.md` - 系统设计指南
- `docs/测试指南.md` - 测试指南