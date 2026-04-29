package com.cosmos.origin.uaa.domain.dos;

import com.cosmos.origin.common.model.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.*;

import java.time.LocalDate;

/**
 * UAA 用户实体（映射 t_user 表）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_user")
public class UaaUserDO extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private LocalDate birthday;

    private String phone;

    private String email;

    private Byte sex;

    private Byte status;

    private String introduction;
}
