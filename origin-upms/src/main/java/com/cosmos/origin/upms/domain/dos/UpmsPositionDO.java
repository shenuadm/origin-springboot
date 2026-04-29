package com.cosmos.origin.upms.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 岗位实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_position")
public class UpmsPositionDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 岗位名称 */
    private String name;

    /** 岗位编码 */
    private String code;

    /** 排序 */
    private Integer sort;

    /** 状态（0-启用 1-禁用） */
    private Byte status;

    /** 备注 */
    private String remark;
}
