package com.cosmos.origin.common.domain.dos;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色DO
 *
 * @author 一陌千尘
 * @date 2025/11/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_role")
public class RoleDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String roleName;

    private String roleKey;
}
