#!/bin/bash

# 微服务生成脚本
# 用法: ./generate-service.sh <服务名> <端口>
# 示例: ./generate-service.sh comment 8082

SERVICE_NAME=$1
PORT=$2

if [ -z "$SERVICE_NAME" ] || [ -z "$PORT" ]; then
    echo "用法: ./generate-service.sh <服务名> <端口>"
    echo "示例: ./generate-service.sh comment 8082"
    exit 1
fi

SERVICE_DIR="../origin-${SERVICE_NAME}-service"
CLASS_NAME="$(echo $SERVICE_NAME | sed 's/^[a-z]/\u&/')ServiceApplication"

# 创建目录结构
echo "创建服务目录: $SERVICE_DIR"
mkdir -p $SERVICE_DIR/src/main/java/com/cosmos/origin/$SERVICE_NAME
mkdir -p $SERVICE_DIR/src/main/resources
mkdir -p $SERVICE_DIR/src/test/java

# 生成 pom.xml
cat > $SERVICE_DIR/pom.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.cosmos</groupId>
        <artifactId>origin-springboot</artifactId>
        <version>\${revision}</version>
    </parent>

    <artifactId>origin-${SERVICE_NAME}-service</artifactId>
    <packaging>jar</packaging>
    <name>origin-${SERVICE_NAME}-service</name>
    <description>${SERVICE_NAME}服务</description>

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
        
        <!-- 业务 API 和实现 -->
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-${SERVICE_NAME}-api</artifactId>
        </dependency>
        <dependency>
            <groupId>com.cosmos</groupId>
            <artifactId>origin-${SERVICE_NAME}-biz</artifactId>
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
        
        <!-- 监控 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
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
EOF

# 生成启动类
cat > $SERVICE_DIR/src/main/java/com/cosmos/origin/$SERVICE_NAME/${CLASS_NAME}.java << EOF
package com.cosmos.origin.${SERVICE_NAME};

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cosmos.origin")
public class ${CLASS_NAME} {

    public static void main(String[] args) {
        ConfigurableEnvironment env = SpringApplication.run(${CLASS_NAME}.class, args).getEnvironment();
        log.info("""
                
                ----------------------------------------------------------
                \t\
                Service: {} 启动成功！\s
                \t\
                Port: {}
                ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"));
    }
}
EOF

# 生成配置文件
cat > $SERVICE_DIR/src/main/resources/application.yml << EOF
spring:
  application:
    name: origin-${SERVICE_NAME}-service
  profiles:
    active: dev
  cloud:
    nacos:
      discovery:
        server-addr: \${NACOS_SERVER_ADDR:localhost:8848}
        namespace: \${NACOS_NAMESPACE:}
        group: \${NACOS_GROUP:DEFAULT_GROUP}
      config:
        server-addr: \${NACOS_SERVER_ADDR:localhost:8848}
        namespace: \${NACOS_NAMESPACE:}
        group: \${NACOS_GROUP:DEFAULT_GROUP}
        file-extension: yaml
        prefix: \${spring.application.name}

server:
  port: ${PORT}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
EOF

# 生成开发环境配置
cat > $SERVICE_DIR/src/main/resources/application-dev.yml << EOF
spring:
  datasource:
    url: \${DB_URL:jdbc:postgresql://localhost:5432/origin_${SERVICE_NAME}}
    username: \${DB_USERNAME:root}
    password: \${DB_PASSWORD:wzw123!@#}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

  redis:
    host: \${REDIS_HOST:localhost}
    port: \${REDIS_PORT:6379}
    password: \${REDIS_PASSWORD:wzw123!@#}
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

# 日志配置
logging:
  level:
    root: INFO
    com.cosmos.origin.${SERVICE_NAME}: DEBUG
    com.alibaba.nacos: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
EOF

echo "服务 origin-${SERVICE_NAME}-service 生成完成！"
echo ""
echo "=========================================="
echo "下一步操作："
echo "=========================================="
echo ""
echo "1. 在根 pom.xml 的 <modules> 中添加:"
echo "   <module>origin-${SERVICE_NAME}-service</module>"
echo ""
echo "2. 在 origin-${SERVICE_NAME}-api 中添加 Feign 接口:"
echo "   @FeignClient(name = \"origin-${SERVICE_NAME}-service\")"
echo "   public interface ${CLASS_NAME}FeignApi { ... }"
echo ""
echo "3. 编译服务模块:"
echo "   mvn clean install -pl origin-${SERVICE_NAME}-service -am"
echo ""
echo "4. 运行服务（确保 Nacos 已启动）:"
echo "   cd origin-${SERVICE_NAME}-service"
echo "   mvn spring-boot:run"
echo ""
echo "5. Docker 部署:"
echo "   创建 Dockerfile 后执行: docker-compose up -d"
