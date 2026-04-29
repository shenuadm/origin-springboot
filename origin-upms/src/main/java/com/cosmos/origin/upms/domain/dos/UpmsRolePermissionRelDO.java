package com.cosmos.origin.upms.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 角色权限关联实体（映射 t_role_permission_rel）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_role_permission_rel")
public class UpmsRolePermissionRelDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long roleId;

    private Long permissionId;
}
