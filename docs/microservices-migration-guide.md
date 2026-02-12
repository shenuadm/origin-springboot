# 项目微服务化迁移指南

本文档说明如何将 origin-springboot 从模块化单体架构迁移为微服务架构。

## 一、当前架构回顾

origin-springboot 采用**模块化单体（Modular Monolith）**架构设计，具备以下特点：

- **Starter 化装配**：基础组件采用 Spring Boot Starter 模式封装
- **接口与实现分离**：业务模块拆分为 `api` 层和 `biz` 实现层
- **去中心化配置**：各模块自持配置逻辑

这种设计使得迁移到微服务架构时，只需最小改动即可完成拆分。

## 二、微服务拆分策略

### 2.1 服务拆分规划

```
origin-platform (父工程)
├── origin-platform-starters (基础组件，复用现有)
│   ├── origin-common
│   ├── origin-jwt-spring-boot-starter
│   ├── origin-redis-spring-boot-starter
│   ├── origin-gateway-spring-boot-starter
│   └── ...
├── origin-auth-service (认证服务，独立部署)
├── origin-admin-service (管理后台服务，独立部署)
├── origin-comment-service (评论服务，独立部署)
├── origin-websocket-service (WebSocket服务，独立部署)
├── origin-file-service (文件服务，独立部署)
└── origin-api-gateway (API网关，独立部署)
```

### 2.2 拆分原则

| 原则 | 说明 |
|------|------|
| 单一职责 | 每个服务只负责一个业务领域 |
| 独立部署 | 每个服务可独立打包、部署、扩缩容 |
| 数据隔离 | 每个服务拥有独立的数据库 |
| 接口契约 | 通过 api 模块定义服务间契约 |

## 三、迁移步骤

### 步骤 1：创建独立服务模块

以 `origin-comment-service` 为例：

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

**添加 Nacos 依赖（已在 origin-spring-cloud-starter 中）：**

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

**启动 Nacos：**

```bash
docker run -d --name nacos -p 8848:8848 -e MODE=standalone nacos/nacos-server:v2.3.0
```

**application.yml 配置：**

```yaml
spring:
  application:
    name: origin-comment-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml

server:
  port: 8082
```

### 步骤 3：改造 api 层添加 Feign 接口

在 `origin-comment-api` 中添加 Feign 客户端：

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

### 步骤 6：配置 API 网关

创建 `origin-api-gateway` 模块作为统一入口：

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

## 四、迁移检查清单

### 4.1 服务拆分检查

- [ ] 确定服务边界（按业务领域划分）
- [ ] 提取公共 api 模块
- [ ] 创建独立服务模块
- [ ] 配置服务注册发现
- [ ] 配置配置中心（Nacos Config）

### 4.2 通信改造检查

- [ ] 同步调用：Feign 客户端
- [ ] 异步通信：MQ（RocketMQ/Kafka）
- [ ] 事件驱动：Spring Cloud Stream

### 4.3 数据改造检查

- [ ] 数据库拆分
- [ ] 分布式 ID 生成（雪花算法）
- [ ] 分布式事务方案
- [ ] 数据一致性保障

### 4.4 基础设施检查

- [ ] API 网关部署
- [ ] **Nacos 服务注册中心部署**（端口 8848）
- [ ] **Nacos 配置中心部署**
- [ ] 监控告警（Prometheus + Grafana）
- [ ] 链路追踪（SkyWalking）
- [ ] 日志收集（ELK）

### 4.5 安全改造检查

- [ ] JWT 认证统一在网关层处理
- [ ] 服务间调用认证（内部 Token）
- [ ] 接口权限控制

## 五、渐进式迁移方案

### 阶段 1：单体 + 远程调用（双写期）

```
┌─────────────────────────────────────┐
│           origin-web                │
│  ┌─────────────┐  ┌─────────────┐  │
│  │  本地调用    │  │  Feign调用   │  │
│  │ comment-biz │  │ comment-api │  │
│  └─────────────┘  └─────────────┘  │
└─────────────────────────────────────┘
```

同时维护本地调用和远程调用，验证服务正确性。

### 阶段 2：服务独立部署

```
┌─────────────┐     ┌─────────────────┐
│ origin-web  │────▶│ comment-service │
└─────────────┘     └─────────────────┘
```

切断本地调用，全部走远程调用。

### 阶段 3：完整微服务（基于 Nacos）

```
                    ┌─────────────┐
                    │    Nacos    │
                    │  注册/配置   │
                    └──────┬──────┘
                           │
┌─────────────┐     ┌──────┴──────┐
│ API Gateway │────▶│   Auth      │
└─────────────┘     └─────────────┘
       │              ┌─────────────┐
       ├─────────────▶│   Admin     │
       │              └─────────────┘
       │              ┌─────────────┐
       └─────────────▶│   Comment   │
                      └─────────────┘
```

所有服务独立部署，通过 Nacos 实现服务注册发现和配置管理，通过网关统一入口。

## 六、常见问题

### Q1: 服务间调用性能问题？

**解决方案：**
- 使用本地缓存（Caffeine）减少重复调用
- 批量接口代替单个查询
- 异步调用（CompletableFuture）
- 服务网格（Istio）优化网络通信

### Q2: 分布式事务如何处理？

**解决方案：**
- 最终一致性：消息队列 + 本地事务表
- Saga 模式：长事务拆分 + 补偿操作
- TCC 模式：Try-Confirm-Cancel

### Q3: 服务拆分粒度？

**建议：**
- 初期：按业务领域粗粒度拆分（3-5 个服务）
- 后期：根据业务增长逐步细化
- 避免过度拆分导致运维复杂度上升

## 七、参考文档

- [Spring Cloud Alibaba 官方文档](https://sca.aliyun.com/)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Cloud Gateway 文档](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [微服务设计模式](https://microservices.io/patterns/index.html)
