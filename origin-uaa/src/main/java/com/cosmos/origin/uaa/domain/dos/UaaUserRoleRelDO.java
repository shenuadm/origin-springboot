package com.cosmos.origin.uaa.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * UAA 用户角色关联实体（映射 t_user_role_rel 表）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_user_role_rel")
public class UaaUserRoleRelDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long userId;

    private Long roleId;
}
