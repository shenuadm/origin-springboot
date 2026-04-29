package com.cosmos.origin.uaa.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * UAA 角色实体（映射 t_role 表）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_role")
public class UaaRoleDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String roleName;

    private String roleKey;
}
