package com.cosmos.origin.uaa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token 请求参数
 */
@Data
@Schema(name = "刷新Token请求参数")
public class RefreshTokenReqVO {

    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "Refresh Token")
    private String refreshToken;
}
