package com.cosmos.origin.upms.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 用户岗位关联实体（映射 t_user_position_rel）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_user_position_rel")
public class UpmsUserPositionRelDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long userId;

    private Long positionId;
}
