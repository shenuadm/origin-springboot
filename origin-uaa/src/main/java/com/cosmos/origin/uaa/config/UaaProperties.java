package com.cosmos.origin.uaa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * UAA 配置属性
 */
@Data
@ConfigurationProperties(prefix = "uaa")
public class UaaProperties {

    /**
     * 签发人地址
     */
    private String issuer = "http://localhost:8846";

    /**
     * Token 配置
     */
    private Token token = new Token();

    /**
     * OAuth2 客户端配置
     */
    private Client client = new Client();

    @Data
    public static class Token {
        /**
         * Access Token 有效期（秒），默认 2 小时
         */
        private Long accessTokenTtl = 7200L;

        /**
         * Refresh Token 有效期（秒），默认 30 天
         */
        private Long refreshTokenTtl = 2592000L;
    }

    @Data
    public static class Client {
        /**
         * 客户端 ID
         */
        private String id = "origin-client";

        /**
         * 客户端密钥（明文，会被 BCrypt 编码）
         */
        private String secret = "origin-secret";

        /**
         * 授权回调地址
         */
        private List<String> redirectUris = List.of("http://127.0.0.1:8081/login/oauth2/code/origin-client");

        /**
         * 授权范围
         */
        private List<String> scopes = List.of("openid", "profile");

        /**
         * 支持的授权类型
         */
        private List<String> grantTypes = List.of("authorization_code", "refresh_token", "client_credentials");
    }
}
