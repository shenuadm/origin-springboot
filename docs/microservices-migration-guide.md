# 项目微服务化迁移指南

本文档说明 origin-springboot 的两种架构模式（单体/微服务）以及如何在这两种模式间切换。

> **注意**：本项目已通过 Maven Profile 实现了单体模式和微服务模式的一键切换，无需手动迁移代码。本文档主要说明架构设计原理和扩展方法。

## 一、架构概述

origin-springboot 支持**单体模式**和**微服务模式**两种架构，通过 Maven Profile 一键切换：

| 特性 | 单体模式 (monolith) | 微服务模式 (microservice) |
|------|---------------------|---------------------------|
| Web 技术 | Spring MVC (Servlet) | Spring WebFlux (Reactive) |
| 网关 | origin-gateway-starter (本地拦截) | Spring Cloud Gateway |
| 服务发现 | 本地依赖注入 | Nacos 服务注册发现 |
| 配置中心 | 本地配置文件 | Nacos 配置中心 |
| 适用场景 | 小型项目、快速开发 | 大型项目、分布式部署 |

### 架构设计特点

- **Starter 化装配**：基础组件采用 Spring Boot Starter 模式封装
- **接口与实现分离**：业务模块拆分为 `api` 层和 `biz` 实现层
- **去中心化配置**：各模块自持配置逻辑
- **Profile 条件加载**：通过 `@Profile` 注解控制组件在不同模式下的加载

这种设计使得两种架构模式可以无缝切换，无需修改业务代码。

## 二、架构模式切换

### 2.1 快速切换

通过 Maven Profile 切换架构模式：

```bash
# 单体模式（默认）
mvn clean package -P monolith -DskipTests

# 微服务模式
mvn clean package -P microservice -DskipTests
```

### 2.2 配置文件说明

| 配置文件 | 用途 |
|----------|------|
| `application.yml` | 单体模式主配置 |
| `application-microservice.yml` | 微服务模式专属配置 |
| `application-dev.yml` | 开发环境配置 |
| `application-prod.yml` | 生产环境配置 |

### 2.3 Profile 条件加载

通过 `@Profile` 注解控制组件加载：

```java
// 仅在单体模式下加载
@Profile("!microservice")
@Configuration
public class WebSecurityConfig { ... }

// 仅在微服务模式下加载
@Profile("microservice")
@Configuration
public class ReactiveSecurityConfig { ... }
```

## 三、微服务拆分策略

### 3.1 服务拆分规划

```
origin-springboot (父工程)
├── origin-framework (基础组件层)
│   ├── origin-common
│   ├── origin-jwt-spring-boot-starter
│   ├── origin-redis-spring-boot-starter
│   ├── origin-gateway-spring-boot-starter
│   └── origin-spring-cloud-starter
├── origin-auth (认证模块)
├── origin-admin (管理后台模块)
│   ├── origin-admin-api (接口契约)
│   └── origin-admin-biz (业务实现)
├── origin-comment (评论模块)
│   ├── origin-comment-api (接口契约)
│   └── origin-comment-biz (业务实现)
└── origin-web (运行入口)
```

### 3.2 拆分原则

| 原则 | 说明 |
|------|------|
| 单一职责 | 每个服务只负责一个业务领域 |
| 独立部署 | 每个服务可独立打包、部署、扩缩容 |
| 数据隔离 | 每个服务拥有独立的数据库 |
| 接口契约 | 通过 api 模块定义服务间契约 |

## 四、创建独立服务模块

如需将业务模块拆分为独立部署的微服务，可参考以下步骤：

### 步骤 1：使用脚本生成服务骨架

项目提供了服务生成脚本，快速创建微服务模块：

```bash
# 使用脚本生成服务
./scripts/generate-service.sh <服务名> <端口>

# 示例：生成评论服务，端口 8082
./scripts/generate-service.sh comment 8082
```

生成的目录结构：

```
origin-comment-service/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/cosmos/origin/comment/
    │   │       └── CommentServiceApplication.java
    │   └── resources/
    │       ├── application.yml
    │       └── application-dev.yml
    └── test/
```

**pom.xml 示例：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <parent>
        <groupId>com.cosmos</groupId>
        <artifactId>origin-springboot</artifactId>
        <version>${revision}</version>
    </parent>
    
    <artifactId>origin-comment-service</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <!-- 基础 Starter -->
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-common</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-jwt-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-redis-spring-boot-starter</artifactId>
        </dependency>
        
        <!-- 业务 API 契约 -->
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-comment-api</artifactId>
        </dependency>
        
        <!-- 业务实现 -->
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-comment-biz</artifactId>
        </dependency>
        
        <!-- 微服务治理 -->
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-spring-cloud-starter</artifactId>
        </dependency>
        
        <!-- 数据库 -->
        <dependency>
            <groupId>com.mybatis-flex</groupId>
            <artifactId>mybatis-flex-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**启动类示例：**

```java
package com.cosmos.origin.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cosmos.origin")
public class CommentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApplication.class, args);
    }
}
```

### 步骤 2：配置服务注册与发现

**Nacos 依赖**已在 `origin-spring-cloud-starter` 中集成，无需额外添加。

**启动 Nacos（Docker）：**

```bash
# 使用 docker-compose 启动
docker-compose up -d nacos

# 或单独启动
docker run -d --name nacos -p 8848:8848 -e MODE=standalone nacos/nacos-server:v2.3.0
```

**Nacos 控制台**：http://localhost:8848/nacos (默认账号密码: nacos/nacos)

**application.yml 配置：**

```yaml
spring:
  application:
    name: origin-comment-service
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
      config:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:}
        group: ${NACOS_GROUP:DEFAULT_GROUP}
        file-extension: yaml
        prefix: ${spring.application.name}

server:
  port: 8082
```

### 步骤 3：在 api 层添加 Feign 接口

在 `origin-comment-api` 模块中添加 Feign 客户端（单体模式下也可直接使用）：

```java
package com.cosmos.origin.comment.api;

import com.cosmos.origin.comment.model.vo.*;
import com.cosmos.origin.common.utils.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "origin-comment-service", path = "/comment")
public interface CommentFeignApi {
    
    @PostMapping("/publish")
    Response<Void> publishComment(@RequestBody PublishCommentReqVO reqVO);
    
    @PostMapping("/list")
    Response<FindCommentListRspVO> findCommentList(@RequestBody FindCommentListReqVO reqVO);
    
    @PostMapping("/page")
    Response<FindCommentPageListRspVO> findCommentPageList(@RequestBody FindCommentPageListReqVO reqVO);
    
    @PostMapping("/delete")
    Response<Void> deleteComment(@RequestBody DeleteCommentReqVO reqVO);
    
    @PostMapping("/examine")
    Response<Void> examineComment(@RequestBody ExamineCommentReqVO reqVO);
}
```

### 步骤 4：改造 Controller 暴露 REST 接口

确保 `CommentController` 使用标准 REST 注解：

```java
package com.cosmos.origin.comment.controller;

import com.cosmos.origin.comment.api.CommentFeignApi;
import com.cosmos.origin.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController implements CommentFeignApi {
    
    private final CommentService commentService;
    
    @Override
    @PostMapping("/publish")
    public Response<Void> publishComment(@RequestBody PublishCommentReqVO reqVO) {
        return commentService.publishComment(reqVO);
    }
    
    // ... 其他方法
}
```

### 步骤 5：服务消费者改造

在 `origin-admin-service` 中调用评论服务：

```java
package com.cosmos.origin.admin.service;

import com.cosmos.origin.comment.api.CommentFeignApi;
import com.cosmos.origin.comment.model.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCommentService {
    
    private final CommentFeignApi commentFeignApi;
    
    public void deleteComment(Long commentId) {
        DeleteCommentReqVO reqVO = new DeleteCommentReqVO();
        reqVO.setId(commentId);
        commentFeignApi.deleteComment(reqVO);
    }
}
```

### 步骤 6：API 网关配置

微服务模式下，网关由 `origin-web` 模块的 `origin-gateway-spring-boot-starter` 提供。

如需独立网关服务，可创建 `origin-api-gateway` 模块：

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 认证服务
        - id: auth-service
          uri: lb://origin-auth-service
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=1
        
        # 评论服务
        - id: comment-service
          uri: lb://origin-comment-service
          predicates:
            - Path=/comment/**
          filters:
            - StripPrefix=1
        
        # 管理后台服务
        - id: admin-service
          uri: lb://origin-admin-service
          predicates:
            - Path=/admin/**
          filters:
            - StripPrefix=1
            # JWT 认证过滤器
            - name: JwtAuth
```

### 步骤 7：数据库拆分

每个服务使用独立的数据库：

```yaml
# origin-comment-service 数据库配置
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/origin_comment
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
```

**数据同步策略：**
- 用户表：通过事件同步或共享用户服务
- 评论表：独立存储在评论服务数据库

### 步骤 8：分布式事务处理

对于跨服务事务，使用 Saga 模式或 TCC 模式：

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final CommentFeignApi commentFeignApi;
    private final InventoryFeignApi inventoryFeignApi;
    
    @Transactional
    public void createOrder(CreateOrderReqVO reqVO) {
        try {
            // 本地事务：创建订单
            orderMapper.insert(order);
            
            // 调用评论服务
            commentFeignApi.publishComment(commentReq);
            
            // 调用库存服务
            inventoryFeignApi.deductInventory(inventoryReq);
            
        } catch (Exception e) {
            // 触发补偿操作
            triggerCompensation(order.getId());
            throw e;
        }
    }
}
```

## 五、架构模式对比

### 5.1 技术栈对比

| 组件 | 单体模式 | 微服务模式 |
|------|----------|------------|
| Web 框架 | Spring MVC | Spring WebFlux |
| 安全框架 | Spring Security Servlet | Spring Security Reactive |
| 网关 | origin-gateway-starter | Spring Cloud Gateway |
| 服务发现 | 本地依赖 | Nacos Discovery |
| 配置中心 | 本地文件 | Nacos Config |
| 会话管理 | Servlet Session | Redis Session |
| WebSocket | Servlet WebSocket | Reactive WebSocket |

### 5.2 性能对比

| 指标 | 单体模式 | 微服务模式 |
|------|----------|------------|
| 启动时间 | 快（约 10-15s） | 较慢（约 15-25s） |
| 内存占用 | 较低 | 较高（Netty + Gateway） |
| 并发处理 | 线程池模型 | 事件循环模型 |
| 水平扩展 | 整体扩展 | 按需扩展 |

## 六、迁移检查清单

### 6.1 服务拆分检查

- [ ] 确定服务边界（按业务领域划分）
- [ ] 提取公共 api 模块
- [ ] 创建独立服务模块
- [ ] 配置服务注册发现
- [ ] 配置配置中心（Nacos Config）

### 6.2 通信改造检查

- [ ] 同步调用：Feign 客户端
- [ ] 异步通信：MQ（RocketMQ/Kafka）
- [ ] 事件驱动：Spring Cloud Stream

### 6.3 数据改造检查

- [ ] 数据库拆分
- [ ] 分布式 ID 生成（雪花算法）
- [ ] 分布式事务方案
- [ ] 数据一致性保障

### 6.4 基础设施检查

- [ ] API 网关部署
- [ ] **Nacos 服务注册中心部署**（端口 8848）
- [ ] **Nacos 配置中心部署**
- [ ] 监控告警（Prometheus + Grafana）
- [ ] 链路追踪（SkyWalking）
- [ ] 日志收集（ELK）

### 6.5 安全改造检查

- [ ] JWT 认证统一在网关层处理
- [ ] 服务间调用认证（内部 Token）
- [ ] 接口权限控制

## 七、渐进式迁移方案

### 阶段 1：单体模式开发（当前）

```
┌─────────────────────────────────────┐
│           origin-web                │
│  ┌─────────────┐  ┌─────────────┐  │
│  │  admin-biz  │  │ comment-biz │  │
│  └─────────────┘  └─────────────┘  │
│                                     │
│  本地依赖注入，快速开发迭代            │
└─────────────────────────────────────┘
```

**特点**：
- 所有模块本地加载
- 使用 Spring MVC + Servlet
- 快速开发、调试方便
- 适合小型项目和初创团队

### 阶段 2：双模式并存验证

```
┌─────────────────────────────────────┐
│           origin-web                │
│  ┌─────────────┐  ┌─────────────┐  │
│  │  本地调用    │  │  Feign调用   │  │
│  │ comment-biz │  │ comment-api │  │
│  └─────────────┘  └─────────────┘  │
│                                     │
│  Maven Profile 切换架构模式          │
│  -P monolith / -P microservice     │
└─────────────────────────────────────┘
```

**特点**：
- 两种模式代码共存
- 通过 Profile 切换验证
- Feign 接口与本地实现并存
- 确保服务拆分可行性

### 阶段 3：微服务独立部署

```
                    ┌─────────────┐
                    │    Nacos    │
                    │  注册/配置   │
                    └──────┬──────┘
                           │
┌─────────────┐     ┌──────┴──────┐
│ API Gateway │────▶│   Auth      │
│  (origin-web)     │  Service    │
└─────────────┘     └─────────────┘
       │              ┌─────────────┐
       ├─────────────▶│   Admin     │
       │              │  Service    │
       │              └─────────────┘
       │              ┌─────────────┐
       └─────────────▶│   Comment   │
                      │  Service    │
                      └─────────────┘
```

**特点**：
- 业务服务独立部署
- 通过 Nacos 服务发现
- 网关统一入口和认证
- 支持按需扩缩容

## 八、常见问题

### Q1: 单体模式和微服务模式如何选择？

**选择单体模式的情况**：
- 团队规模小（< 10人）
- 业务复杂度低
- 需要快速迭代开发
- 没有专职运维团队

**选择微服务模式的情况**：
- 团队规模大，需要并行开发
- 业务复杂，需要独立部署和扩展
- 有专职运维/DevOps 团队
- 需要多语言技术栈

### Q2: 两种模式可以共存吗？

可以。本项目通过 Maven Profile 和 Spring Profile 实现了两种模式的无缝切换：

```bash
# 单体模式打包
mvn clean package -P monolith

# 微服务模式打包
mvn clean package -P microservice
```

代码层面通过 `@Profile` 注解控制组件加载，业务代码无需修改。

### Q3: 服务间调用性能问题？

**解决方案：**
- 使用本地缓存（Caffeine）减少重复调用
- 批量接口代替单个查询
- 异步调用（CompletableFuture）
- 服务网格（Istio）优化网络通信

### Q4: 分布式事务如何处理？

**解决方案：**
- 最终一致性：消息队列 + 本地事务表
- Saga 模式：长事务拆分 + 补偿操作
- TCC 模式：Try-Confirm-Cancel

### Q5: 服务拆分粒度？

**建议：**
- 初期：按业务领域粗粒度拆分（3-5 个服务）
- 后期：根据业务增长逐步细化
- 避免过度拆分导致运维复杂度上升

## 九、参考文档

- [Spring Cloud Alibaba 官方文档](https://sca.aliyun.com/)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Cloud Gateway 文档](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Spring WebFlux 文档](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [微服务设计模式](https://microservices.io/patterns/index.html)
