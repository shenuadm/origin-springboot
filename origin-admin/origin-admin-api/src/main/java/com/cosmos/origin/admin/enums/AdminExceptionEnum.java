package com.cosmos.origin.admin.enums;

import com.cosmos.origin.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理模块异常码枚举
 * <p>
 * 定义用户管理、角色管理、权限管理等模块的异常码
 *
 * @author 一陌千尘
 * @date 2025/02/06
 */
@Getter
@AllArgsConstructor
public enum AdminExceptionEnum implements BaseExceptionInterface {

    // ----------- 用户管理异常码 -----------
    USER_NOT_FOUND("30000", "用户不存在"),
    USER_ALREADY_EXISTS("30001", "用户已存在"),
    USER_DISABLED("30002", "用户已被禁用"),
    USER_PASSWORD_ERROR("30003", "密码错误"),
    USER_PASSWORD_SAME_AS_OLD("30004", "新密码不能与旧密码相同"),
    USER_CANNOT_DELETE_SELF("30005", "不能删除当前登录用户"),
    USER_ROLE_NOT_FOUND("30006", "用户角色不存在"),
    USER_NO_PERMISSION("30007", "用户无权限执行此操作"),

    // ----------- 角色管理异常码 -----------
    ROLE_NOT_FOUND("30010", "角色不存在"),
    ROLE_ALREADY_EXISTS("30011", "角色已存在"),
    ROLE_CANNOT_DELETE("30012", "该角色下存在用户，无法删除"),
    ROLE_NAME_DUPLICATE("30013", "角色名称已存在"),
    ROLE_CODE_DUPLICATE("30014", "角色标识已存在"),
    ROLE_IS_SYSTEM_ROLE("30015", "系统角色不可操作"),

    // ----------- 权限管理异常码 -----------
    PERMISSION_NOT_FOUND("30020", "权限不存在"),
    PERMISSION_ALREADY_EXISTS("30021", "权限已存在"),
    PERMISSION_CANNOT_DELETE("30022", "该权限下存在角色，无法删除"),
    PERMISSION_NAME_DUPLICATE("30023", "权限名称已存在"),
    PERMISSION_CODE_DUPLICATE("30024", "权限标识已存在"),

    // ----------- 会话管理异常码 -----------
    SESSION_NOT_FOUND("30030", "会话不存在"),
    SESSION_EXPIRED("30031", "会话已过期"),
    SESSION_ALREADY_ONLINE("30032", "用户已在线"),
    USER_ALREADY_LOGGED_IN("30033", "用户已在其他设备登录"),
    ;

    // 异常码
    private final String errorCode;

    // 错误信息
    private final String errorMessage;
}
