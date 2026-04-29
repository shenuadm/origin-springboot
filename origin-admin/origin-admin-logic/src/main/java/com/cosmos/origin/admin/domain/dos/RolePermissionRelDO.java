package com.cosmos.origin.admin.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 角色权限关联实体类
 *
 * @author 一陌千尘
 * @date 2026/02/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_role_permission_rel")
public class RolePermissionRelDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permissionId;
}
