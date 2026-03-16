# 配置指南

本文档详细介绍 origin-springboot 项目的所有配置项。

## 配置项总览

| 配置前缀 | 说明 | 模块 |
|----------|------|------|
| `server.*` | 服务器配置 | Spring Boot |
| `spring.*` | Spring 核心配置 | Spring Boot |
| `jwt.*` | JWT 认证配置 | origin-jwt |
| `login.*` | 登录安全策略 | origin-admin |
| `origin.*` | 自定义组件配置 | origin-framework |
| `springdoc.*` | API 文档配置 | SpringDoc |
| `knife4j.*` | Knife4j 配置 | Knife4j |
| `comment.*` | 评论功能配置 | origin-comment |

## 详细配置

### 服务基础配置

```yaml
server:
  port: 8081  # 服务端口
```

### JWT 认证配置

```yaml
jwt:
  issuer: "一陌千尘"                    # 签发人
  secret: ${JWT_SECRET:}                # 秘钥（生产环境建议使用环境变量）
  tokenExpireTime: 1440                # Token 过期时间（分钟）
  rememberMeExpireTime: 10080          # 记住我 Token 过期时间（分钟）
  tokenHeaderKey: Authorization        # Token 请求头 Key
  tokenPrefix: Bearer                  # Token 请求头前缀
```

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| jwt.issuer | String | - | JWT 签发者 |
| jwt.secret | String | - | JWT 秘钥（建议使用环境变量） |
| jwt.tokenExpireTime | Integer | 1440 | 普通 Token 过期时间（分钟） |
| jwt.rememberMeExpireTime | Integer | 10080 | 记住我 Token 过期时间（分钟） |
| jwt.tokenHeaderKey | String | Authorization | Token 请求头名称 |
| jwt.tokenPrefix | String | Bearer | Token 请求头前缀 |

### 登录安全策略配置

```yaml
login:
  attempt:
    enabled: false                     # 是否开启登录次数限制
    max-attempts: 5                   # 最大失败次数
    lock-duration-minutes: 30         # 账号锁定时间（分钟）
  session:
    strategy: multiple                 # 登录策略：single/multiple
```

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| login.attempt.enabled | Boolean | false | 是否开启登录次数限制 |
| login.attempt.max-attempts | Integer | 5 | 最大失败次数 |
| login.attempt.lock-duration-minutes | Integer | 30 | 账号锁定时间 |
| login.session.strategy | String | multiple | 登录策略：single-单设备登录，multiple-多设备登录 |

### 事件驱动线程池配置

```yaml
origin:
  event:
    thread-pool:
      core-pool-size: 4                # 核心线程数
      max-pool-size: 8                 # 最大线程数
      queue-capacity: 200              # 队列容量
      keep-alive-seconds: 60           # 空闲线程存活时间
      thread-name-prefix: "event-executor-"  # 线程名前缀
      wait-for-tasks-to-complete-on-shutdown: true
      await-termination-seconds: 60
```

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| origin.event.thread-pool.core-pool-size | Integer | CPU核心数 | 核心线程数 |
| origin.event.thread-pool.max-pool-size | Integer | CPU核心数*2 | 最大线程数 |
| origin.event.thread-pool.queue-capacity | Integer | 200 | 队列容量 |
| origin.event.thread-pool.keep-alive-seconds | Integer | 60 | 空闲线程存活时间 |
| origin.event.thread-pool.thread-name-prefix | String | event-executor- | 线程名前缀 |

### 定时任务线程池配置

```yaml
origin:
  scheduler:
    thread-pool:
      pool-size: 5                     # 线程池大小
      thread-name-prefix: "scheduler-" # 线程名前缀
      wait-for-tasks-to-complete-on-shutdown: true
      await-termination-seconds: 60
```

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| origin.scheduler.thread-pool.pool-size | Integer | - | 线程池大小 |
| origin.scheduler.thread-pool.thread-name-prefix | String | - | 线程名前缀 |

### 网关配置

```yaml
origin:
  gateway:
    enabled: true                      # 是否启用网关功能
    rate-limit:
      enabled: false                  # 是否启用限流
      default-limit: 100              # 默认限流阈值
      time-window: 1                  # 限流时间窗口
      limit-message: "请求过于频繁，请稍后再试"  # 限流提示
    white-list:                        # 白名单路径
      - /swagger-ui
      - /v3/api-docs/**
    black-list: [ ]                    # 黑名单
```

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| origin.gateway.enabled | Boolean | true | 是否启用网关功能 |
| origin.gateway.rate-limit.enabled | Boolean | false | 是否启用限流 |
| origin.gateway.rate-limit.default-limit | Integer | 100 | 默认限流阈值 |
| origin.gateway.rate-limit.time-window | Integer | 1 | 限流时间窗口（秒） |
| origin.gateway.white-list | List | - | 白名单路径 |
| origin.gateway.black-list | List | - | 黑名单 |

### 评论功能配置

```yaml
comment:
  examine:
    open: false                       # 是否开启评论审核
  sensi:
    word:
      open: false                     # 是否开启敏感词过滤
```

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| comment.examine.open | Boolean | false | 是否开启评论审核 |
| comment.sensi.word.open | Boolean | false | 是否开启敏感词过滤 |

### API 文档配置

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /v3/api-docs
  group-configs:
    - group: 'admin'
      display-name: 'Admin 后台管理接口'
      paths-to-match: '/admin/**'
      packages-to-scan:
        - com.cosmos.origin.admin.controller
```

### Knife4j 配置

```yaml
knife4j:
  enable: true
  setting:
    language: zh_cn
```

## 环境变量

在生产环境中，建议使用环境变量管理敏感配置：

```bash
# 数据库密码
DB_PASSWORD=your_password

# JWT 秘钥
JWT_SECRET=your_jwt_secret

# Redis 密码
REDIS_PASSWORD=your_redis_password

# MinIO 密钥
MINIO_SECRET_KEY=your_minio_key
```

## 配置文件加载顺序

1. `application.yml` - 默认配置
2. `application-{profile}.yml` - 环境特定配置
3. 环境变量 - 覆盖配置文件
4. 命令行参数 - 最高优先级

## 微服务模式额外配置

在微服务模式下，还需要配置 Nacos：

```yaml
spring:
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
      username: nacos
      password: nacos
      config:
        namespace: origin-config
        group: DEFAULT_GROUP
      discovery:
        namespace: origin-service
        group: DEFAULT_GROUP
```
