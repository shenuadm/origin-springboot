package com.cosmos.origin.admin.model.vo.user;

import com.cosmos.origin.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询用户分页数据请求参数")
public class FindUserPageListReqVO extends BasePageQuery {

    @Schema(description = "用户昵称")
    private String nickname;
}
