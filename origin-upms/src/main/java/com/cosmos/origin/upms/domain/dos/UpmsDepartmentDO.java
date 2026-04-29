package com.cosmos.origin.upms.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

import java.util.List;

/**
 * 部门实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_department")
public class UpmsDepartmentDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 父部门 ID，顶级为 0 */
    private Long parentId;

    /** 部门名称 */
    private String name;

    /** 部门编码 */
    private String code;

    /** 排序 */
    private Integer sort;

    /** 状态（0-启用 1-禁用） */
    private Byte status;

    /** 备注 */
    private String remark;

    /** 子部门列表（非数据库字段） */
    @Column(ignore = true)
    private List<UpmsDepartmentDO> children;
}
