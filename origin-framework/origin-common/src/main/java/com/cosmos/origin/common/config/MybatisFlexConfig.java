package com.cosmos.origin.common.config;

import com.mybatisflex.core.audit.AuditManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis Flex 全局配置
 * <p>
 * 负责开启SQL审计功能，Mapper扫描由各业务模块自行配置
 *
 * @author 一陌千尘
 * @date 2025/11/03
 */
@Configuration
@ConditionalOnClass(AuditManager.class)
public class MybatisFlexConfig {

    private static final Logger logger = LoggerFactory
            .getLogger("mybatis-flex-sql");

    public MybatisFlexConfig() {
        // 开启审计功能
        AuditManager.setAuditEnable(true);

        // 设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
                logger.info("SQL语句：{}, 执行耗时：{}ms", auditMessage.getFullSql()
                        , auditMessage.getElapsedTime())
        );
    }
}
