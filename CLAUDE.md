# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

origin-springboot 是一个现代化的 Spring Boot 技术底座项目，采用 Spring Boot 4.0.6 + Spring Cloud 2025.1.1 技术栈构建。项目采用模块化单体 (Modular Monolith) 架构设计，支持单体模式和微服务模式通过 Maven Profile 一键切换。

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
├── origin-framework (基础框架层，12个 Starter)
│   ├── origin-common                    # 通用工具组件
│   ├── origin-jwt-spring-boot-starter  # JWT认证组件
│   ├── origin-jackson-spring-boot-starter
│   ├── origin-operationlog-spring-boot-starter
│   ├── origin-event-spring-boot-starter
│   ├── origin-scheduler-spring-boot-starter
│   ├── origin-websocket-spring-boot-starter  # 包含 ChatRoomController
│   ├── origin-redis-spring-boot-starter
│   ├── origin-oss-spring-boot-starter        # 包含 FileController
│   ├── origin-gateway-spring-boot-starter
│   ├── origin-config-spring-boot-starter
│   └── origin-spring-cloud-starter     # Nacos + Sentinel + Feign + LoadBalancer
├── origin-auth           # 认证服务模块
├── origin-uaa            # OAuth2 统一认证授权服务 (port 8846)
├── origin-upms           # 统一权限管理服务 (port 7070)
├── origin-admin          # 管理后台聚合模块
│   ├── origin-admin-api  # 接口层（VO、Enums、API 接口）
│   ├── origin-admin-logic # 业务逻辑层（DO、Mapper、ServiceImpl，可复用 jar）
│   └── origin-admin-biz  # 业务表现层（Controller，thin layer）
├── origin-comment        # 评论模块聚合
│   ├── origin-comment-api
│   ├── origin-comment-logic
│   └── origin-comment-biz
├── origin-web           # 单体/微服务运行入口（唯一启动模块），不含业务 Controller
├── origin-gateway        # API 网关服务（WebFlux + 安全过滤器链）
└── origin-example        # 示例代码模块
```

### Module Roles

| 模块 | 单体模式 | 微服务模式 |
|------|---------|-----------|
| origin-web | 单体运行入口（唯一启动模块），不含业务 Controller | 业务模块入口（唯一启动模块），可按业务划分添加新模块 |
| origin-admin | 业务模块（管理后台 API），拆分为 api/logic/biz 三层 | 独立微服务 |
| origin-comment | 业务模块（评论 API），拆分为 api/logic/biz 三层 | 独立微服务 |
| origin-uaa | OAuth2 授权服务器 | 独立微服务 (port 8846) |
| origin-upms | 统一权限管理（用户/角色/权限/部门/岗位） | 独立微服务 (port 7070) |
| origin-gateway | 可选（可集成到 web） | 独立网关服务（路由 + 鉴权 + 防注入） |

**三层分离模式 (api / logic / biz):**
- `api`: VO、Enums、Service 接口定义 — 供其他模块依赖调用
- `logic`: DO、Mapper、ServiceImpl — 纯业务逻辑 jar，无启动类，可被 monolith 聚合或 microservice 复用
- `biz`: Controller、AutoConfiguration — 薄层，仅负责 HTTP 入口和参数转换

**origin-web 模块说明**：
- 单体模式：作为唯一启动模块，仅包含启动类、Knife4j 配置、Web 安全配置、登录限流过滤器
- 微服务模式：作为业务模块入口，可根据业务需求拆分为独立的微服务模块
- 所有业务 Controller 已迁移至各自模块（admin-biz、comment-biz、websocket-starter、oss-starter）

### Design Patterns

1. **Starter 化装配**: 基础组件采用 Spring Boot Starter 模式封装，通过 AutoConfiguration 自动装配
2. **三层分离**: 业务模块拆分为 `api`（契约）、`logic`（可复用逻辑）、`biz`（HTTP 入口）
3. **统一响应结构**: 所有 API 通过 `Response` 类统一封装，分页查询使用 `PageResponse`
4. **全局异常处理**: 通过 `GlobalExceptionHandler` 统一处理 `BizException` 和系统异常
5. **API 日志记录**: 使用 `@ApiOperationLog` 注解 + AOP 记录请求入参、出参和耗时
6. **VO 模式**: 请求使用 ReqVO，响应使用 RspVO，严格区分业务层和展示层
7. **模块开关**: 单体模式下通过 `origin.module.*` 条件注解启用/禁用业务模块

## Code Organization

| 类型 | 位置 | 说明 |
|------|------|------|
| DO (Data Object) | `domain.dos` | 数据库实体对象，继承 `BaseEntity` |
| Mapper | `domain.mapper` | MyBatis Flex 接口 |
| Service Interface | `service` | 业务逻辑接口定义 |
| Service Impl | `service.impl` | 业务逻辑实现（位于 `*-logic` 模块） |
| Controller | `controller` | 控制器层（位于 `*-biz` 模块或 Starter） |
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
    @Operation(summary = "获取用户分页列表")
    @ApiOperationLog(description = "获取用户分页列表")
    public PageResponse<?> findUserPageList(FindUserPageListReqVO reqVO) {
        return userService.findUserPageList(reqVO);
    }
}
```

## Gateway Security Filter Chain

`origin-gateway` 基于 Spring Cloud Gateway (WebFlux) 实现全局安全过滤器链：

| Filter | Order | 职责 |
|--------|-------|------|
| `AuthGlobalFilter` | -100 | 白名单放行、拦截外部伪造内部请求头、Bearer Token 格式校验 |
| `SqlInjectionGlobalFilter` | -50 | GET 查询参数 / POST/PUT JSON 和表单体的 SQL 关键字黑名单检测 |

**Feign 请求头传播**: `origin-spring-cloud-starter` 中的 `FeignRequestInterceptor` 自动透传 `Authorization`、`X-Request-Id`、`X-Real-IP`、`X-Forwarded-For` 以及所有 `X-Origin-*` 自定义请求头。

## Monolith Module Toggles

在单体模式下，业务模块可通过 `application.yml` 开关：

```yaml
origin:
  module:
    admin: true
    comment: true
    auth: true
    oss: true
    websocket: true
```

对应的 `@ConditionalOnProperty(prefix = "origin.module", name = "xxx", havingValue = "true")` 注解控制 Controller 和 Service 的加载。

## Key Technologies

- Java 25 + Spring Boot 4.0.6 + Spring Cloud 2025.1.1
- PostgreSQL 42.7.8 + MyBatis Flex 1.11.6
- Redis + Redisson 3.27.0 (分布式锁)
- JWT (jjwt 0.11.2) + Spring Security + Spring Authorization Server
- Sentinel 限流熔断（微服务模式）
- Knife4j 4.6.0 API 文档

## Important Paths

- 单体应用入口: `origin-web/src/main/java/com/cosmos/origin/web/OriginWebApplication.java`
- UAA 应用入口: `origin-uaa/src/main/java/com/cosmos/origin/uaa/UaaApplication.java`
- UPMS 应用入口: `origin-upms/src/main/java/com/cosmos/origin/upms/UpmsApplication.java`
- Gateway 应用入口: `origin-gateway/src/main/java/com/cosmos/origin/gateway/GatewayApplication.java`
- 组件扫描: `com.cosmos.origin.*`
- 单体主配置: `origin-web/src/main/resources/application.yml`
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
