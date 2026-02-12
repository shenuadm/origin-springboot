## 项目概述

origin-springboot 是一个技术底座单体版项目，采用 Spring Boot 3.5.10 + PostgreSQL + JWT + MyBatis Flex 技术栈构建。项目主要功能包括用户登录、角色管理、用户管理、评论管理、操作日志等模块。

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+ (可选，用于登录限流)

### 启动步骤

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd origin-springboot
   ```

2. **初始化数据库**
   ```bash
   # 创建数据库并执行初始化脚本
   psql -U postgres -f docs/sql/origin.sql
   ```

3. **配置环境变量（可选）**
   ```bash
   # 开发环境使用默认值即可，生产环境建议配置
   set DB_PASSWORD=your-password
   set JWT_SECRET=your-secret-key
   ```

4. **编译运行**
   ```bash
   mvn clean install -DskipTests
   cd origin-web
   mvn spring-boot:run
   ```

5. **访问应用**
   - 应用地址: http://localhost:8081
   - API 文档: http://localhost:8081/doc.html

## 项目架构

这是一个采用 **模块化单体 (Modular Monolith)** 架构设计的项目，兼顾了单体开发的便利性和微服务演进的灵活性。

### 核心模块结构

```
origin-springboot (父工程)
├── origin-framework (基础框架层)
│   ├── origin-common (通用工具组件，提供 RequestUtil、JsonUtil 等工具类)
│   ├── origin-jwt-spring-boot-starter (JWT认证组件，Starter)
│   ├── origin-jackson-spring-boot-starter (Jackson序列化组件，Starter)
│   ├── origin-event-spring-boot-starter (事件驱动组件，Starter)
│   ├── origin-operationlog-spring-boot-starter (操作日志组件，Starter)
│   ├── origin-oss-spring-boot-starter (对象存储组件，Starter)
│   ├── origin-scheduler-spring-boot-starter (定时任务组件，Starter)
│   ├── origin-websocket-spring-boot-starter (WebSocket组件，Starter)
│   ├── origin-redis-spring-boot-starter (Redis缓存组件，Starter)
│   ├── origin-gateway-spring-boot-starter (API网关组件，Starter)
│   └── origin-spring-cloud-starter (微服务治理组件，Starter)
├── origin-auth (认证服务模块)
├── origin-admin (管理后台聚合模块)
│   ├── origin-admin-api (管理后台接口层，包含 VO、Enums、API)
│   └── origin-admin-biz (管理后台业务实现)
├── origin-comment (评论模块聚合)
│   ├── origin-comment-api (评论接口层，包含 VO、Enums、API)
│   └── origin-comment-biz (评论业务实现)
├── origin-web (单体运行入口，唯一的启动模块)
└── origin-example (示例代码模块)
```

### 设计理念：单体架构，微服务演进

1.  **Starter 化装配**：基础组件（Common, JWT, Log）采用 Spring Boot Starter 模式封装，通过 `AutoConfiguration` 自动装配，无需在启动类手动扫描，方便模块独立拆分或跨项目复用。
2.  **接口与实现分离**：业务模块拆分为 `api` 层（DTO/Enum/Client）和业务实现层。单体模式下直接依赖实现类，演进到微服务时，只需在 `api` 层增加 Feign 接口并将依赖切换为远程调用。
3.  **去中心化配置**：各模块自持配置逻辑，入口模块 `origin-web` 仅负责运行时的环境聚合。

### 关键架构模式

1. **统一响应结构**: 所有 API 通过 `Response` 类统一封装返回结果，分页查询使用 `PageResponse`

2. **全局异常处理**: 通过 `GlobalExceptionHandler` 统一处理业务异常 `BizException` 和系统异常

3. **API 日志记录**: 使用 `@ApiOperationLog` 注解 + AOP 切面记录 API 请求入参、出参和耗时，每个请求分配唯一 traceId

4. **安全认证**: 基于 Spring Security + JWT 实现认证授权
   - 登录流程通过 `JwtAuthenticationFilter` 处理
   - Token 验证通过 `TokenAuthenticationFilter` 处理
   - 支持登录失败限流、登录尝试次数记录
   - 支持单设备/多设备登录策略配置

5. **数据访问层**: 使用 MyBatis Flex 作为 ORM 框架，DO (Data Object) 对象使用 Lombok 简化代码

6. **VO 模式**: 请求使用 ReqVO，响应使用 RspVO，严格区分业务层和展示层的数据结构

7. **事件驱动**: 内置事件发布订阅机制，支持异步事件处理，解耦业务逻辑

8. **定时任务**: 支持动态定时任务调度，可配置线程池参数

9. **工具类复用**: `RequestUtil` 统一处理客户端 IP 获取，`JsonUtil` 统一处理 JSON 序列化，避免代码重复

## 常用命令

### 构建和运行

```bash
# 编译整个项目
mvn clean install

# 编译并跳过测试
mvn clean install -DskipTests

# 运行应用（在 origin-web 目录下）
cd origin-web
mvn spring-boot:run

# 或者直接运行打包后的 jar
java -jar origin-web/target/origin-web-0.0.1-SNAPSHOT.jar

# 指定环境运行（dev/prod）
java -jar origin-web/target/origin-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### 测试

```bash
# 运行所有测试
mvn test

# 运行特定模块的测试
mvn test -pl origin-admin

# 运行特定测试类
mvn test -Dtest=OriginWebApplicationTests
```

### 其他常用命令

```bash
# 查看依赖树
mvn dependency:tree

# 清理编译产物
mvn clean

# 只编译不打包
mvn compile
```

## 开发规范

### 代码组织

- **DO (Data Object)**: 数据库实体对象，位于 `domain.dos` 包下，继承 `BaseEntity`
- **Mapper**: MyBatis 接口，位于 `domain.mapper` 包下
- **Service**: 业务逻辑层，接口和实现分离
- **Controller**: 控制器层，负责接收请求和返回响应
- **VO**: 视图对象
  - ReqVO: 请求参数对象，位于 `model.vo.*` 包下，需要进行参数校验
  - RspVO: 响应结果对象，位于 `model.vo.*` 包下
- **Util**: 工具类，位于 `origin-common` 模块的 `utils` 包下，供全项目复用

### 命名规范

- Service 方法命名: `find*`, `add*`, `update*`, `delete*`
- VO 命名: `{操作}{实体}{Req/Rsp}VO`，例如 `AddUserReqVO`, `FindUserPageListRspVO`
- Controller 接口路径: 使用 RESTful 风格，例如 `/admin/user/*`

### 重要配置

- **应用入口**: `origin-web/src/main/java/com/cosmos/origin/web/OriginWebApplication.java`
- **组件扫描**: 必须扫描 `com.cosmos.origin.*` 包才能识别所有模块的组件
- **配置文件**:
  - `origin-web/src/main/resources/application.yml` (主配置)
  - `application-dev.yml` (开发环境)
  - `application-prod.yml` (生产环境)
- **API 文档**: 启动后访问 `http://localhost:8081/doc.html`
- **JWT 配置**: 在 `application.yml` 中配置 JWT 密钥、过期时间等

### 技术栈版本

| 技术 | 版本 |
|------|------|
| Spring Boot | 3.5.10 |
| Java | 17 |
| Spring Cloud | 2025.0.0 |
| Spring AI | 1.1.1 |
| MyBatis Flex | 1.11.5 |
| PostgreSQL | 42.7.8 |
| HikariCP | 7.0.2 |
| JWT (jjwt) | 0.11.2 |
| Knife4j | 4.6.0 |
| MinIO | 8.2.1 |
| Lombok | 1.18.42 |
| Guava | 33.5.0-jre |

### 数据库

- 使用 PostgreSQL 42.7.8
- 连接池使用 HikariCP 7.0.2
- ORM 框架使用 MyBatis Flex 1.11.5
- 数据库初始化脚本位于 `docs/sql/origin.sql`
- 数据库连接配置在 `application-dev.yml` 中
- 支持通过环境变量覆盖配置，详见 [环境变量配置](#环境变量配置)

### 安全相关

- 密码使用 `PasswordEncoder` 加密存储
- JWT Token 放在请求头 `Authorization: Bearer {token}`
- 登录接口默认为 `/login`，可通过配置自定义
- 支持登录失败限流和尝试次数记录
- 支持单设备登录（互踢）和多设备登录策略
- 内置 IP2Region 支持 IP 地址解析

### API 开发流程

1. 在对应模块的 `model.vo` 包下创建 ReqVO 和 RspVO
2. 在 Service 接口中定义业务方法
3. 在 Service 实现类中实现业务逻辑
4. 在 Controller 中添加接口，使用 `@ApiOperationLog` 记录日志
5. 使用 Knife4j 注解完善 API 文档

### 日志记录

- 在 Controller 方法上添加 `@ApiOperationLog(description = "功能描述")` 自动记录 API 请求日志
- 日志会自动记录：请求入参、出参、耗时、请求类、请求方法
- 每个请求分配唯一 traceId 用于链路追踪
- 访问日志（AccessLog）可通过调整日志级别关闭：`logging.level.com.cosmos.origin.gateway.interceptor.AccessLogInterceptor=WARN`

### 工具类使用

项目提供以下通用工具类，位于 `origin-common` 模块：

| 工具类 | 路径 | 功能说明 |
|--------|------|----------|
| `RequestUtil` | `com.cosmos.origin.common.utils.RequestUtil` | 获取客户端真实 IP（支持 X-Forwarded-For 等代理头） |
| `JsonUtil` | `com.cosmos.origin.common.utils.JsonUtil` | JSON 序列化/反序列化（基于 Jackson） |
| `Response` | `com.cosmos.origin.common.utils.Response` | 统一响应结果封装 |
| `PageResponse` | `com.cosmos.origin.common.utils.PageResponse` | 分页响应结果封装 |

**使用示例：**
```java
// 获取客户端 IP
String clientIp = RequestUtil.getClientIp(request);

// JSON 序列化
String json = JsonUtil.toJsonString(obj);

// 统一响应
return Response.success(data);
return Response.fail("错误信息");
```

## 环境变量配置

项目支持通过环境变量配置敏感信息，优先级高于配置文件中的默认值。

### 数据库配置

| 环境变量 | 说明 | 默认值 (dev) |
|----------|------|--------------|
| `DB_URL` | 数据库连接URL | `jdbc:postgresql://127.0.0.1:5432/origin?serverTimezone=Asia/Shanghai` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `wzw123!@#` (dev) / 必填 (prod) |

### Redis 配置

| 环境变量 | 说明 | 默认值 |
|----------|------|--------|
| `REDIS_HOST` | Redis 主机地址 | `127.0.0.1` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `REDIS_PASSWORD` | Redis 密码 | `wzw123!@#` (dev) / 必填 (prod) |

### MinIO 配置

| 环境变量 | 说明 | 默认值 (dev) |
|----------|------|--------------|
| `MINIO_ENDPOINT` | MinIO 服务端点 | `http://127.0.0.1:9000` |
| `MINIO_ACCESS_KEY` | 访问密钥 | `minioAdmin` |
| `MINIO_SECRET_KEY` | 秘密密钥 | `wzw123!@#` (dev) / 必填 (prod) |

### JWT 配置

| 环境变量 | 说明 | 默认值 |
|----------|------|--------|
| `JWT_SECRET` | JWT 签名密钥 | 配置文件中的默认值（建议生产环境必配） |

### 配置示例

**Windows:**
```cmd
set DB_PASSWORD=mypassword
set JWT_SECRET=my-secret-key
set REDIS_PASSWORD=my-redis-password
```

**Linux/Mac:**
```bash
export DB_PASSWORD=mypassword
export JWT_SECRET=my-secret-key
export REDIS_PASSWORD=my-redis-password
```

**IDEA 中配置:**
在 Run Configuration 的 Environment variables 中添加：
```
DB_PASSWORD=mypassword;JWT_SECRET=my-secret-key
```
