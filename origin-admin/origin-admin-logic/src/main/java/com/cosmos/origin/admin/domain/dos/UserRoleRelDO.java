package com.cosmos.origin.admin.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 用户角色关联 实体类
 *
 * @author 一陌千尘
 * @date 2025/11/05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_user_role_rel")
public class UserRoleRelDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long userId;

    private Long roleId;
}
