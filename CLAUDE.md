# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

origin-springboot is a Spring Boot 3.5.10 technical foundation project with modular monolith architecture. It supports both **monolith** and **microservice** modes via Maven Profile switching.

## Build Commands

```bash
# Monolith mode (default)
mvn clean package -P monolith -DskipTests

# Microservice mode (requires Nacos)
mvn clean package -P microservice -DskipTests

# Run tests
mvn test

# Run specific module tests
mvn test -pl origin-admin

# Run specific test class
mvn test -Dtest=OriginWebApplicationTests

# Run single test method
mvn test -Dtest=OriginWebApplicationTests#testMethodName
```

## Architecture

### Module Structure
```
origin-springboot (parent)
├── origin-framework (infrastructure layer with Spring Boot Starters)
│   ├── origin-common              # Utilities (RequestUtil, JsonUtil, Response, etc.)
│   ├── origin-jwt-spring-boot-starter    # JWT authentication
│   ├── origin-jackson-spring-boot-starter # JSON serialization
│   ├── origin-operationlog-spring-boot-starter  # API logging with @ApiOperationLog
│   ├── origin-event-spring-boot-starter # Event-driven (async)
│   ├── origin-scheduler-spring-boot-starter    # Dynamic scheduled tasks
│   ├── origin-websocket-spring-boot-starter     # WebSocket support
│   ├── origin-redis-spring-boot-starter # Redis caching/distributed lock
│   ├── origin-oss-spring-boot-starter   # MinIO object storage
│   ├── origin-gateway-spring-boot-starter      # API gateway starter
│   ├── origin-config-spring-boot-starter        # Nacos configuration
│   └── origin-spring-cloud-starter     # Microservice governance
├── origin-auth              # Authentication service
├── origin-admin             # Admin module (api + biz split)
│   ├── origin-admin-api     # VO, Enums, API interfaces
│   └── origin-admin-biz    # Service implementation, Controller
├── origin-comment          # Comment module (api + biz split)
│   ├── origin-comment-api  # VO, Enums, API interfaces
│   └── origin-comment-biz # Service implementation, Controller
├── origin-web              # Entry module (runs in monolith mode)
├── origin-gateway          # API Gateway service (standalone microservice)
└── origin-example          # Example code
```

### Code Organization Pattern

- **API+Biz Split**: Business modules follow the `api` + `biz` pattern
  - `api` module: VO definitions, Enums, Service interfaces (for future microservices)
  - `biz` module: Service implementations, Controllers, DO/Mapper
- **DO (Data Object)**: Database entity, extends `BaseEntity`, located in `domain.dos`
- **Mapper**: MyBatis Flex interface, located in `domain.mapper`
- **Service**: Interface in `service`, implementation in `service.impl`
- **Controller**: REST endpoints, uses `@ApiOperationLog` for request logging
- **VO**: Request/Response objects
  - ReqVO: Request parameters in `model.vo.*`, use `@Validated` for validation
  - RspVO: Response data in `model.vo.*`

### Architecture Modes

| Mode | Profile | Description |
|------|---------|-------------|
| Monolith | `monolith` | All modules loaded locally, uses Servlet + Spring MVC |
| Microservice | `microservice` | Services registered with Nacos, uses Spring Cloud Gateway + WebFlux |

Gateway runs as a standalone service in microservice mode, authenticating tokens before routing requests.

### Key Design Patterns

1. **Unified Response**: Use `Response<T>` for API responses, `PageResponse` for paginated results
2. **Global Exception Handling**: `GlobalExceptionHandler` handles `BizException` and system exceptions
3. **API Logging**: Add `@ApiOperationLog(description = "...")` to controller methods
4. **JWT Authentication**: `JwtAuthenticationFilter` for login, `TokenAuthenticationFilter` for token validation
5. **Event-Driven**: Use `EventPublisher` to publish events, `ApplicationListener` to handle async
6. **VO Pattern**: Strict separation between request (ReqVO) and response (RspVO) data structures

## Running the Application

```bash
# Build first
mvn clean package -P monolith -DskipTests

# Run from origin-web module
cd origin-web
mvn spring-boot:run -P monolith

# Or run the JAR directly
java -jar origin-web/target/origin-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Microservice mode (requires Nacos running)
java -jar origin-web/target/origin-web-0.0.1-SNAPSHOT.jar --spring.profiles.active=microservice,dev

# Run API Gateway (microservice mode, separate process)
java -jar origin-gateway/target/origin-gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Access API docs at: http://localhost:8081/doc.html

## Key Configuration

| Item | Location | Notes |
|------|----------|-------|
| Application entry | `origin-web/src/main/java/.../OriginWebApplication.java` | Scan `com.cosmos.origin.*` |
| Gateway entry | `origin-gateway/src/main/java/.../OriginGatewayApplication.java` | Separate gateway service |
| Main config | `origin-web/src/main/resources/application.yml` | Server port 8081 |
| Gateway config | `origin-gateway/src/main/resources/application.yml` | Gateway port 8080 |
| Dev config | `origin-web/src/main/resources/application-dev.yml` | Database, Redis settings |
| JWT config | `application.yml` `jwt.*` section | Token expiry, secret key |
| Database init | `docs/sql/origin.sql` | PostgreSQL schema |

### Environment Variables (Production)

```bash
DB_PASSWORD=mypassword
JWT_SECRET=your-secret-key
REDIS_PASSWORD=my-redis-password
MINIO_SECRET_KEY=my-minio-key
NACOS_SERVER_ADDR=127.0.0.1:8848
```

## Common Development Tasks

### Adding a New API
1. Create ReqVO/RspVO in `model.vo` package of api module
2. Define method in Service interface
3. Implement in Service impl class
4. Add endpoint in Controller with `@ApiOperationLog`
5. Use `Response.success(data)` or `Response.fail(message)` to return

### Using Utilities
```java
// Get client IP
String clientIp = RequestUtil.getClientIp(request);

// JSON serialization
String json = JsonUtil.toJsonString(obj);

// Unified response
return Response.success(data);
return Response.fail("Error message");

// Paginated response
return PageResponse.success(list, total, pageNum, pageSize);
```

### Using Events
```java
// Publish event (async)
eventPublisher.publishEvent(new YourEvent(source));

// Handle event
@EventListener
public void handleEvent(YourEvent event) { }
```

## Tech Stack

- Spring Boot 3.5.10
- Java 17
- PostgreSQL + MyBatis Flex
- Redis + Redisson
- JWT (jjwt 0.11.2)
- Spring Cloud 2025.0.0 (microservice mode)
- Nacos 2.3+ (microservice mode)
- Knife4j 4.6.0 (API docs)
