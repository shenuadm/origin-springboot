package com.cosmos.origin.upms.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * UPMS 角色实体（映射 t_role）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_role")
public class UpmsRoleDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String roleName;

    private String roleKey;

    private Byte status;

    private Integer sort;

    private String remark;
}
