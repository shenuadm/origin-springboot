# API 设计规范

## 响应格式

### 统一响应结构

所有 API 响应使用 `Response<T>` 包装：

```json
{
  "success": true,
  "message": "操作成功",
  "errorCode": null,
  "data": { ... }
}
```

### 成功响应

```java
// 无返回数据
return Response.success();

// 有返回数据
return Response.success(user);

// 自定义消息
return Response.success("用户创建成功");
```

### 失败响应

```java
// 无错误信息
return Response.fail();

// 错误消息
return Response.fail("用户名已存在");

// 错误码 + 错误消息
return Response.fail("30001", "用户已存在");

// 使用枚举
return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID);

// 使用业务异常
return Response.fail(new BizException(AdminExceptionEnum.USER_NOT_FOUND));
```

### 分页响应

```java
// 分页查询
return PageResponse.success(records, total, current, size);
```

响应结构：
```json
{
  "success": true,
  "message": null,
  "errorCode": null,
  "data": {
    "records": [...],
    "total": 100,
    "current": 1,
    "size": 10
  }
}
```

## 请求格式

### 分页请求

```java
public class FindUserPageListReqVO {
    @Schema(description = "当前页码")
    private Long current = 1L;

    @Schema(description = "每页数量")
    private Long size = 10L;

    @Schema(description = "昵称（模糊查询）")
    private String nickname;
}
```

### 添加请求

```java
public class AddUserReqVO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
}
```

### 更新请求

```java
public class UpdateUserReqVO {
    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private String avatar;
}
```

### 删除请求

```java
public class DeleteUserReqVO {
    @NotNull(message = "用户ID不能为空")
    private Long id;
}
```

## VO 命名规范

| 类型 | 命名规范 | 示例 |
|------|----------|------|
| 分页请求 | {业务}PageListReqVO | FindUserPageListReqVO |
| 分页响应 | {业务}PageListRspVO | FindUserPageListRspVO |
| 详情请求 | Find{业务}InfoReqVO | FindUserInfoReqVO |
| 详情响应 | Find{业务}InfoRspVO | FindUserInfoRspVO |
| 添加请求 | Add{业务}ReqVO | AddUserReqVO |
| 更新请求 | Update{业务}ReqVO | UpdateUserReqVO |
| 删除请求 | Delete{业务}ReqVO | DeleteUserReqVO |
| 选择列表 | {业务}SelectReqVO | RoleSelectReqVO |

## 异常码规范

### 通用异常码

| 异常码 | 说明 |
|--------|------|
| 10000 | 系统错误 |
| 10001 | 参数错误 |

### 业务异常码

业务异常码采用分段方式：

| 范围 | 模块 |
|------|------|
| 20000-20999 | 认证/授权 |
| 30000-30999 | 用户管理 |
| 30010-30019 | 角色管理 |
| 30020-30029 | 权限管理 |
| 30030-30039 | 会话管理 |
| 40000-40999 | 业务模块A |
| 50000-50999 | 业务模块B |

## Controller 规范

### RESTful 风格

```java
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AdminUserServiceApi userService;

    @GetMapping("/page")
    @ApiOperation("获取用户分页列表")
    @ApiOperationLog(description = "获取用户分页列表")
    public PageResponse<?> findUserPageList(FindUserPageListReqVO reqVO) {
        return userService.findUserPageList(reqVO);
    }

    @PostMapping
    @ApiOperation("添加用户")
    @ApiOperationLog(description = "添加用户")
    public Response<?> addUser(@Valid @RequestBody AddUserReqVO reqVO) {
        return userService.addUser(reqVO);
    }

    @PutMapping
    @ApiOperation("更新用户")
    @ApiOperationLog(description = "更新用户")
    public Response<?> updateUser(@Valid @RequestBody UpdateUserReqVO reqVO) {
        return userService.updateUser(reqVO);
    }

    @DeleteMapping
    @ApiOperation("删除用户")
    @ApiOperationLog(description = "删除用户")
    public Response<?> deleteUser(@Valid @RequestBody DeleteUserReqVO reqVO) {
        return userService.deleteUser(reqVO);
    }

    @GetMapping("/info")
    @ApiOperation("获取当前用户信息")
    @ApiOperationLog(description = "获取当前用户信息")
    public Response<?> getUserInfo() {
        return userService.findUserInfo();
    }
}
```

### 注解使用

- `@RestController`: 标识 REST 控制器
- `@RequestMapping`: 路由前缀
- `@ApiOperation`: API 文档描述
- `@ApiOperationLog`: 请求日志记录
- `@Valid`: 参数校验
- `@RequestBody`: 请求体绑定

## 参数校验

### 常用校验注解

| 注解 | 说明 |
|------|------|
| @NotNull | 不能为 null |
| @NotBlank | 不能为空字符串 |
| @NotEmpty | 不能为空（集合/数组） |
| @Size | 长度范围 |
| @Min/Max | 数值范围 |
| @Email | 邮箱格式 |
| @Pattern | 正则表达式 |
| @Valid | 嵌套对象校验 |

### 分组校验

```java
public class UserVO {
    @NotNull(groups = {Update.class}, message = "用户ID不能为空")
    private Long id;

    @NotBlank(groups = {Add.class}, message = "用户名不能为空")
    private String username;
}
```

## 日志记录

使用 `@ApiOperationLog` 自动记录请求日志：

```java
@PostMapping
@ApiOperation("创建订单")
@ApiOperationLog(description = "创建订单")  // 记录操作描述
public Response<?> createOrder(@Valid @RequestBody CreateOrderReqVO reqVO) {
    // 业务逻辑
    return Response.success();
}
```

日志内容包括：
- 请求 IP
- 请求 URL
- 请求方法
- 请求参数
- 响应状态
- 执行时间
- 操作描述
