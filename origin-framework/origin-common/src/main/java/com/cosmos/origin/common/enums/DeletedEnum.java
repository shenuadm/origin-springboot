package com.cosmos.origin.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 逻辑删除枚举
 *
 * @author 一陌千尘
 * @date 2026/02/13
 */
@Getter
@AllArgsConstructor
public enum DeletedEnum {

    YES(true),
    NO(false);

    private final Boolean value;
}
