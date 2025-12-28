package com.cosmos.origin.common.config;

import com.mybatisflex.core.audit.AuditManager;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis Flex 配置
 *
 * @author 一陌千尘
 * @date 2025/11/03
 */
@Configuration
@MapperScan("com.cosmos.origin.common.domain.mapper")
public class MybatisFlexConfig {

    private static final Logger logger = LoggerFactory
            .getLogger("mybatis-flex-sql");

    public MybatisFlexConfig() {
        // 开启审计功能
        AuditManager.setAuditEnable(true);

        // 设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("{},{}ms", auditMessage.getFullSql()
                        , auditMessage.getElapsedTime())
        );
    }
}
