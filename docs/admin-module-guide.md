# Admin 模块开发指南

## 模块结构

```
origin-admin
├── origin-admin-api        # API 接口定义层
│   ├── constants/          # 常量定义
│   ├── enums/              # 枚举定义
│   ├── model/              # VO 模型
│   │   └── vo/             # 请求/响应对象
│   └── service/            # 服务接口定义
│
└── origin-admin-biz        # 业务实现层
    ├── config/             # 配置类
    ├── controller/        # 控制器
    ├── domain/            # 领域模型
    │   ├── dos/           # 数据对象
    │   └── mapper/        # MyBatis Flex Mapper
    ├── service/           # 服务接口及实现
    └── utils/             # 工具类
```

## API 接口

### 用户管理

#### AdminUserServiceApi

用户管理服务接口，定义用户相关的业务操作。

**方法列表：**

| 方法名 | 说明 | 参数 | 返回值 |
|--------|------|------|--------|
| updatePassword | 修改密码 | UpdateAdminUserPasswordReqVO | Response |
| findUserInfo | 获取当前登录用户信息 | - | Response |
| findUserPageList | 用户分页查询 | FindUserPageListReqVO | PageResponse |
| addUser | 添加用户 | AddUserReqVO | Response |
| updateUser | 更新用户 | UpdateUserReqVO | Response |
| deleteUser | 删除用户 | DeleteUserReqVO | Response |
| unlockUser | 解锁用户账号 | UnlockUserReqVO | Response |

### 角色管理

#### AdminRoleServiceApi

角色管理服务接口，定义角色相关的业务操作。

**方法列表：**

| 方法名 | 说明 | 参数 | 返回值 |
|--------|------|------|--------|
| findRolePageList | 角色分页查询 | FindRolePageListReqVO | PageResponse |
| add | 添加角色 | AddRoleReqVO | Response |
| update | 更新角色 | UpdateRoleReqVO | Response |
| delete | 删除角色 | DeleteRoleReqVO | Response |
| findRoleSelectList | 获取角色 Select 列表 | - | Response |

### 评论管理

#### AdminCommentServiceApi

评论管理服务接口，定义评论审核相关的业务操作。

**方法列表：**

| 方法名 | 说明 | 参数 | 返回值 |
|--------|------|------|--------|
| findCommentPageList | 评论分页查询 | FindCommentPageListReqVO | PageResponse |
| deleteComment | 删除评论 | DeleteCommentReqVO | Response |
| examineComment | 审核评论 | ExamineCommentReqVO | Response |

### 会话管理

#### UserSessionServiceApi

用户会话服务接口，定义用户登录会话管理操作。

**方法列表：**

| 方法名 | 说明 | 参数 | 返回值 |
|--------|------|------|--------|
| saveSession | 保存用户会话 | UserSessionVO, expireMinutes | void |
| getSessionByUsername | 根据用户名获取会话 | username | UserSessionVO |
| getAllSessionsByUsername | 获取用户所有设备会话 | username | List<UserSessionVO> |
| getSessionByToken | 根据 Token 获取会话 | token | UserSessionVO |
| removeSession | 删除用户会话 | username | void |
| removeSessionByToken | 删除指定 Token 的会话 | token | void |
| getAllOnlineSessions | 获取所有在线会话 | - | List<UserSessionVO> |
| forceLogout | 强制下线用户 | username | void |
| refreshSessionExpire | 刷新会话过期时间 | username, expireMinutes | void |
| isOnline | 检查用户是否在线 | username | boolean |
| getOnlineDeviceCount | 获取用户在线设备数量 | username | int |

### 登录限制

#### LoginAttemptServiceApi

登录尝试限制服务接口，定义登录安全控制操作。

**方法列表：**

| 方法名 | 说明 | 参数 | 返回值 |
|--------|------|------|--------|
| checkLocked | 检查用户是否被锁定 | username | void |
| loginFailed | 记录登录失败 | username | void |
| loginSuccess | 记录登录成功 | username | void |
| getRemainingAttempts | 获取剩余尝试次数 | username | int |
| unlock | 手动解锁账号 | username | void |
| isLocked | 检查是否被锁定 | username | boolean |
| getLockRemainingMinutes | 获取锁定剩余时间 | username | long |
| getAttemptInfo | 获取尝试详情 | username | Map |
| isEnabled | 功能是否启用 | - | boolean |

## 常量定义

### AdminConstants

管理模块常量类，定义各类业务常量。

**用户相关常量：**

| 常量名 | 说明 | 默认值 |
|--------|------|--------|
| DEFAULT_PASSWORD | 默认密码 | 123456 |
| USERNAME_MIN_LENGTH | 用户名最小长度 | 4 |
| USERNAME_MAX_LENGTH | 用户名最大长度 | 20 |
| PASSWORD_MIN_LENGTH | 密码最小长度 | 6 |
| PASSWORD_MAX_LENGTH | 密码最大长度 | 20 |
| NICKNAME_MAX_LENGTH | 昵称最大长度 | 50 |

**角色相关常量：**

| 常量名 | 说明 | 默认值 |
|--------|------|--------|
| SUPER_ADMIN_ROLE_CODE | 超级管理员角色标识 | SUPER_ADMIN |
| ADMIN_ROLE_CODE | 管理员角色标识 | ADMIN |
| USER_ROLE_CODE | 普通用户角色标识 | USER |

**会话相关常量：**

| 常量名 | 说明 | 默认值 |
|--------|------|--------|
| DEFAULT_SESSION_EXPIRE_MINUTES | 默认会话过期时间 | 120 分钟 |
| LOGIN_STRATEGY_SINGLE | 单设备登录策略 | single |
| LOGIN_STRATEGY_MULTIPLE | 多设备登录策略 | multiple |

## 异常定义

### AdminExceptionEnum

管理模块异常码枚举。

**用户管理异常码：**

| 异常码 | 说明 |
|--------|------|
| 30000 | 用户不存在 |
| 30001 | 用户已存在 |
| 30002 | 用户已被禁用 |
| 30003 | 密码错误 |
| 30004 | 新密码不能与旧密码相同 |
| 30005 | 不能删除当前登录用户 |

**角色管理异常码：**

| 异常码 | 说明 |
|--------|------|
| 30010 | 角色不存在 |
| 30011 | 角色已存在 |
| 30012 | 该角色下存在用户，无法删除 |
| 30015 | 系统角色不可操作 |

## 使用示例

### 注入服务

```java
@RestController
@RequiredArgsConstructor
public class UserController {

    private final AdminUserServiceApi adminUserService;

    @GetMapping("/user/info")
    public Response<?> getUserInfo() {
        return adminUserService.findUserInfo();
    }
}
```

### 使用常量

```java
// 验证用户名长度
if (username.length() < AdminConstants.USERNAME_MIN_LENGTH
    || username.length() > AdminConstants.USERNAME_MAX_LENGTH) {
    throw new BizException("用户名长度必须在4-20个字符之间");
}
```

### 抛出业务异常

```java
throw new BizException(AdminExceptionEnum.USER_NOT_FOUND);
```

## 扩展开发

### 添加新的服务接口

1. 在 `origin-admin-api` 模块的 `service` 包中定义接口
2. 在 `origin-admin-biz` 模块的 `service` 包中实现接口
3. 在实现类上添加 `@Service` 注解

### 添加新的异常码

在 `AdminExceptionEnum` 中添加新的枚举值，遵循以下规则：
- 异常码必须唯一
- 错误信息必须清晰描述问题
- 按功能模块分组编号
