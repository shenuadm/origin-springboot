package com.cosmos.origin.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleTypeEnum {

    SYSTEM_ADMIN("ROLE_SYSTEM_ADMIN", "系统管理员"),
    DESIGNER("ROLE_ORG_ADMIN", "组织管理员"),
    OWNER("ROLE_PART_ADMIN", "部门管理员"),
    PRODUCTION_ADMIN("ROLE_ORG_USER", "组织用户"),
    CONSTRUCTION_WORKER("ROLE_USER", "普通用户"),
    VISITOR("ROLE_VISITOR", "演示用户"),
    ;

    private final String roleKey;
    private final String roleName;
}
