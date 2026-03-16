# 数据库设计文档

## 概述

本文档描述 origin-springboot 项目的数据库表结构设计。

## ER 图

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│     t_user      │     │   t_user_role   │     │     t_role      │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ id (PK)         │────<│ user_id (FK)    │     │ id (PK)         │
│ username        │     │ role_id (FK)    │>────│ role_name       │
│ password        │     │ id (PK)         │     │ role_code       │
│ nickname        │     └─────────────────┘     │ role_type       │
│ avatar          │                             │ description     │
│ phone           │                             │ create_time     │
│ email           │                             │ update_time     │
│ is_deleted      │                             │ is_deleted      │
│ create_time     │                             └─────────────────┘
│ update_time     │                                   │
└─────────────────┘                                   │
      │                                              │
      │                 ┌─────────────────┐          │
      │                 │  t_role_perm    │          │
      └───────────────<│ role_id (FK)   │<─────────┘
                       │ perm_id (FK)   │
                       │ id (PK)        │
                       └─────────────────┘
                              │
                              │                         ┌─────────────────┐
                              └────────────────────────│   t_permission  │
                                                     ├─────────────────┤
                                                     │ id (PK)         │
                                                     │ perm_name       │
                                                     │ perm_code       │
                                                     │ description     │
                                                     │ create_time     │
                                                     │ update_time     │
                                                     │ is_deleted      │
                                                     └─────────────────┘


┌─────────────────┐     ┌─────────────────┐
│   t_login_log   │     │    t_comment    │
├─────────────────┤     ├─────────────────┤
│ id (PK)         │     │ id (PK)         │
│ username        │     │ content         │
│ ip_address      │     │ avatar          │
│ login_location  │     │ nickname        │
│ browser         │     │ router_url      │
│ os              │     │ create_time     │
│ status          │     │ reply_comment_id│
│ message         │     │ parent_comment_id│
│ user_agent      │     │ reason          │
│ create_time     │     │ status          │
└─────────────────┘     │ username        │
                        │ is_deleted      │
                        └─────────────────┘
```

## 表结构说明

### 用户表 (t_user)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PK, 自增 |
| username | VARCHAR(50) | 用户名 | NOT NULL, UNIQUE |
| password | VARCHAR(200) | 密码 | NOT NULL |
| nickname | VARCHAR(50) | 昵称 | - |
| avatar | VARCHAR(200) | 头像 | - |
| phone | VARCHAR(20) | 手机号 | - |
| email | VARCHAR(100) | 邮箱 | - |
| is_deleted | BOOLEAN | 是否删除 | DEFAULT FALSE |
| create_time | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP |

### 角色表 (t_role)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PK, 自增 |
| role_name | VARCHAR(50) | 角色名称 | NOT NULL |
| role_code | VARCHAR(50) | 角色标识 | NOT NULL, UNIQUE |
| role_type | INT | 角色类型 | - |
| description | VARCHAR(200) | 描述 | - |
| is_deleted | BOOLEAN | 是否删除 | DEFAULT FALSE |
| create_time | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP |

### 权限表 (t_permission)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PK, 自增 |
| perm_name | VARCHAR(50) | 权限名称 | NOT NULL |
| perm_code | VARCHAR(100) | 权限标识 | NOT NULL, UNIQUE |
| description | VARCHAR(200) | 描述 | - |
| is_deleted | BOOLEAN | 是否删除 | DEFAULT FALSE |
| create_time | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP |

### 用户角色关联表 (t_user_role)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PK, 自增 |
| user_id | BIGINT | 用户ID | FK -> t_user.id |
| role_id | BIGINT | 角色ID | FK -> t_role.id |

### 角色权限关联表 (t_role_permission)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PK, 自增 |
| role_id | BIGINT | 角色ID | FK -> t_role.id |
| perm_id | BIGINT | 权限ID | FK -> t_permission.id |

### 登录日志表 (t_login_log)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PK, 自增 |
| username | VARCHAR(50) | 用户名 | NOT NULL |
| ip_address | VARCHAR(50) | IP地址 | - |
| login_location | VARCHAR(100) | 登录地点 | - |
| browser | VARCHAR(50) | 浏览器 | - |
| os | VARCHAR(50) | 操作系统 | - |
| status | INT | 登录状态 | NOT NULL |
| message | VARCHAR(200) | 提示消息 | - |
| user_agent | VARCHAR(500) | User-Agent | - |
| create_time | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |

### 评论表 (t_comment)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PK, 自增 |
| content | VARCHAR(120) | 评论内容 | NOT NULL |
| avatar | VARCHAR(160) | 头像 | - |
| nickname | VARCHAR(60) | 昵称 | NOT NULL |
| router_url | VARCHAR(60) | 所属路由 | NOT NULL |
| reply_comment_id | BIGINT | 回复的评论ID | FK -> t_comment.id |
| parent_comment_id | BIGINT | 父评论ID | FK -> t_comment.id |
| reason | VARCHAR(300) | 审核原因 | - |
| status | SMALLINT | 状态 | NOT NULL |
| username | VARCHAR(60) | 用户名 | NOT NULL |
| is_deleted | BOOLEAN | 是否删除 | DEFAULT FALSE |
| create_time | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP |

## 索引设计

| 表名 | 索引名 | 字段 | 唯一 | 说明 |
|------|--------|------|------|------|
| t_user | uk_username | username | YES | 用户名唯一 |
| t_role | uk_role_code | role_code | YES | 角色标识唯一 |
| t_permission | uk_perm_code | perm_code | YES | 权限标识唯一 |
| t_user_role | uk_user_role | user_id, role_id | YES | 用户-角色唯一 |
| t_role_permission | uk_role_perm | role_id, perm_id | YES | 角色-权限唯一 |

## 关系说明

### 用户-角色关系
- 一对多关系：一个用户可以有多个角色
- 通过 t_user_role 关联表实现

### 角色-权限关系
- 一对多关系：一个角色可以有多个权限
- 通过 t_role_permission 关联表实现

### 评论自关联
- 一对多关系：一条评论可以有多条回复
- 通过 reply_comment_id 和 parent_comment_id 实现

## 数据字典

### 角色类型 (t_role.role_type)

| 值 | 说明 |
|----|------|
| 1 | 系统角色 |
| 2 | 自定义角色 |

### 登录状态 (t_login_log.status)

| 值 | 说明 |
|----|------|
| 1 | 登录成功 |
| 0 | 登录失败 |
| -1 | 账号被锁定 |

### 评论状态 (t_comment.status)

| 值 | 说明 |
|----|------|
| 1 | 待审核 |
| 2 | 正常 |
| 3 | 审核未通过 |

## 设计原则

1. **逻辑删除**：所有业务表使用 `is_deleted` 字段实现逻辑删除
2. **时间戳**：所有表包含 `create_time` 和 `update_time` 字段
3. **主键策略**：使用 BIGINT 自增作为主键
4. **命名规范**：
   - 表名使用小写字母和下划线
   - 字段名使用小写字母和下划线
   - 索引名使用 `uk_` 前缀表示唯一索引
5. **外键约束**：关联表使用外键确保数据完整性
