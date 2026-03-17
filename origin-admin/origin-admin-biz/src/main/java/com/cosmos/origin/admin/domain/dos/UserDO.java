package com.cosmos.origin.admin.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author 一陌千尘
 * @date 2025/11/03
 */
@EqualsAndHashCode(callSuper = true) // 告诉 JVM 不需要生成 equals() 和 hashCode() 方法，直接使用父类的实现
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_user")
public class UserDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 出生日期
     */
    private LocalDateTime birthday;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别（0-女 1-男）
     */
    private Byte sex;

    /**
     * 状态（0-启用 1-禁用）
     */
    private Byte status;

    /**
     * 个人简介
     */
    private String introduction;
}
