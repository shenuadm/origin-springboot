package com.cosmos.origin.admin.model.vo.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 用户信息 VO
 *
 * @author 一陌千尘
 * @date 2026/02/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "WebSocket 用户信息")
public class UserInfoForWebSocketVO {

    @Schema(description = "用户 ID")
    private Long id;

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;
}
