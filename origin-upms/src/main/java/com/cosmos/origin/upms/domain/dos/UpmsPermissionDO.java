package com.cosmos.origin.upms.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

import java.util.List;

/**
 * UPMS 权限/菜单实体（映射 t_permission）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_permission")
public class UpmsPermissionDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 父 ID */
    private Long parentId;

    /** 权限名称 */
    private String name;

    /** 类型（1-目录 2-菜单 3-按钮） */
    private Byte type;

    /** 菜单路由 */
    private String menuUrl;

    /** 菜单图标 */
    private String menuIcon;

    /** 排序 */
    private Integer sort;

    /** 权限标识 */
    private String permissionKey;

    /** 状态（0-启用 1-禁用） */
    private Byte status;

    /** 子权限列表（非数据库字段） */
    @Column(ignore = true)
    private List<UpmsPermissionDO> children;
}
