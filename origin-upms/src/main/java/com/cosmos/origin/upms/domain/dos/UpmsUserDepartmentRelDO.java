package com.cosmos.origin.upms.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 用户部门关联实体（映射 t_user_department_rel）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_user_department_rel")
public class UpmsUserDepartmentRelDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long userId;

    private Long departmentId;

    /** 是否主部门（0-否 1-是） */
    private Byte isPrimary;
}
