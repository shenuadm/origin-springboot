package com.cosmos.origin.jwt.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Refresh Token 请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "刷新 Token 请求参数")
public class RefreshTokenReqVO {

    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "Refresh Token", required = true)
    private String refreshToken;
}
