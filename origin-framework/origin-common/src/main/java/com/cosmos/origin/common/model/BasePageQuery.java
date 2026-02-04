package com.cosmos.origin.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BasePageQuery {

    @Schema(description = "当前页码, 默认第 1 页")
    private Long current = 1L;

    @Schema(description = "每页展示的数据数量，默认每页展示 10 条数据")
    private Long size = 10L;
}
