package com.cosmos.origin.admin.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

/**
 * 权限实体类
 *
 * @author 一陌千尘
 * @date 2026/02/13
 */
@EqualsAndHashCode(callSuper = true) // 告诉 JVM 不需要生成 equals() 和 hashCode() 方法，直接使用父类的实现
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_permission")
public class PermissionDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 父 ID
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 类型（1-目录 2-菜单 3-按钮）
     */
    private Byte type;

    /**
     * 菜单路由
     */
    private String menuUrl;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 权限标识
     */
    private String permissionKey;

    /**
     * 状态（0-启用 1-禁用）
     */
    private Byte status;
}
