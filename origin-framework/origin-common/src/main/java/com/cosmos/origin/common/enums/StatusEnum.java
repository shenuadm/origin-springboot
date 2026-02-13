package com.cosmos.origin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态枚举
 *
 * @author 一陌千尘
 * @date 2026/02/13
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    // 启用
    ENABLE(0),
    // 禁用
    DISABLED(1);

    private final Integer value;
}
