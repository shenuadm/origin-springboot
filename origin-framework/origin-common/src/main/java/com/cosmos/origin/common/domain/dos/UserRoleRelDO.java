package com.cosmos.origin.common.domain.dos;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("t_user_role_rel")
public class UserRoleRelDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long userId;

    private Long roleId;
}

