package com.cosmos.origin.common.domain.dos;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户DO
 *
 * @author 一陌千尘
 * @date 2025/11/03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("t_user")
public class UserDO {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    @Column(onInsertValue = "CURRENT_TIMESTAMP")
    private LocalDateTime createTime;
    @Column(onInsertValue = "CURRENT_TIMESTAMP", onUpdateValue = "CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;
    @Column(onInsertValue = "false")
    private Boolean isDeleted;
}
