-- ===========================================
-- UPMS 新增表：部门、岗位、用户部门关联、用户岗位关联
-- ===========================================

-- 部门表
CREATE TABLE IF NOT EXISTS t_department (
    id              BIGSERIAL PRIMARY KEY,
    parent_id       BIGINT NOT NULL DEFAULT 0,
    name            VARCHAR(100) NOT NULL,
    code            VARCHAR(100) NOT NULL UNIQUE,
    sort            INT NOT NULL DEFAULT 0,
    status          SMALLINT NOT NULL DEFAULT 0,
    remark          VARCHAR(500),
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE
);
COMMENT ON TABLE t_department IS '部门表';
COMMENT ON COLUMN t_department.parent_id IS '父部门 ID，顶级为 0';
COMMENT ON COLUMN t_department.status IS '状态（0-启用 1-禁用）';

-- 岗位表
CREATE TABLE IF NOT EXISTS t_position (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    code            VARCHAR(100) NOT NULL UNIQUE,
    sort            INT NOT NULL DEFAULT 0,
    status          SMALLINT NOT NULL DEFAULT 0,
    remark          VARCHAR(500),
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE
);
COMMENT ON TABLE t_position IS '岗位表';
COMMENT ON COLUMN t_position.status IS '状态（0-启用 1-禁用）';

-- 用户部门关联表
CREATE TABLE IF NOT EXISTS t_user_department_rel (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    department_id   BIGINT NOT NULL,
    is_primary      SMALLINT NOT NULL DEFAULT 0,
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_user_dept UNIQUE (user_id, department_id)
);
COMMENT ON TABLE t_user_department_rel IS '用户部门关联表';
COMMENT ON COLUMN t_user_department_rel.is_primary IS '是否主部门（0-否 1-是）';

-- 用户岗位关联表
CREATE TABLE IF NOT EXISTS t_user_position_rel (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    position_id     BIGINT NOT NULL,
    create_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_user_pos UNIQUE (user_id, position_id)
);
COMMENT ON TABLE t_user_position_rel IS '用户岗位关联表';

-- ===========================================
-- 可选：初始化数据
-- ===========================================

-- 默认部门
INSERT INTO t_department (parent_id, name, code, sort, status, remark)
VALUES (0, '默认部门', 'default', 0, 0, '系统默认部门')
ON CONFLICT (code) DO NOTHING;

-- 默认岗位
INSERT INTO t_position (name, code, sort, status, remark)
VALUES ('默认岗位', 'default', 0, 0, '系统默认岗位')
ON CONFLICT (code) DO NOTHING;
