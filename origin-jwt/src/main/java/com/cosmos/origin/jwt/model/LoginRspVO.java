package com.cosmos.origin.jwt.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "登录响应参数")
public class LoginRspVO {

    @Schema(description = "Token 值")
    private String token;

    @Schema(description = "用户角色列表")
    private List<String> roles;
}
