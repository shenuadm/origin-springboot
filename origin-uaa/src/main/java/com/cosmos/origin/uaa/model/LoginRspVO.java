package com.cosmos.origin.uaa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "登录响应参数")
public class LoginRspVO {

    @Schema(description = "Access Token")
    private String token;

    @Schema(description = "Refresh Token")
    private String refreshToken;

    @Schema(description = "Token 类型")
    private String tokenType;

    @Schema(description = "有效期（秒）")
    private Long expiresIn;

    @Schema(description = "用户角色列表")
    private List<String> roles;
}
