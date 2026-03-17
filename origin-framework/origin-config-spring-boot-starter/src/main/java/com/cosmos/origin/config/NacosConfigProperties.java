package com.cosmos.origin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Nacos 配置属性
 * <p>
 * 统一管理 Nacos 连接配置
 *
 * @author 一陌千尘
 * @date 2025/02/16
 */
@Data
@ConfigurationProperties(prefix = "origin.config.nacos")
public class NacosConfigProperties {

    /**
     * 是否启用 Nacos 配置中心
     */
    private boolean enabled = true;

    /**
     * Nacos 服务器地址
     */
    private String serverAddr = "localhost:8848";

    /**
     * 命名空间
     */
    private String namespace = "";

    /**
     * 分组名称
     */
    private String group = "DEFAULT_GROUP";

    /**
     * 配置文件格式
     */
    private String fileExtension = "yaml";

    /**
     * 是否启用远程配置
     */
    private boolean remoteEnabled = true;

    /**
     * 共享配置列表
     */
    private String sharedDataIds = "";

    /**
     * 共享配置刷新
     */
    private boolean refreshableDataIds = true;

}
