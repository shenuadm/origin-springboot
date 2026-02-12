/*
 Navicat Premium Dump SQL

 Source Server         : localhost-postgresql
 Source Server Type    : PostgreSQL
 Source Server Version : 170006 (170006)
 Source Host           : localhost:5432
 Source Catalog        : origin
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 170006 (170006)
 File Encoding         : 65001

 Date: 12/02/2026 18:49:16
*/


-- ----------------------------
-- Sequence structure for t_comment_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."t_comment_id_seq";
CREATE SEQUENCE "public"."t_comment_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for t_login_log_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."t_login_log_id_seq";
CREATE SEQUENCE "public"."t_login_log_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for t_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."t_user_id_seq";
CREATE SEQUENCE "public"."t_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for t_user_role_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."t_user_role_id_seq";
CREATE SEQUENCE "public"."t_user_role_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_comment";
CREATE TABLE "public"."t_comment" (
  "id" int8 NOT NULL DEFAULT nextval('t_comment_id_seq'::regclass),
  "content" varchar(120) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "avatar" varchar(160) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "nickname" varchar(60) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "router_url" varchar(60) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "reply_comment_id" int8,
  "parent_comment_id" int8,
  "reason" varchar(300) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "status" int2 NOT NULL DEFAULT 1,
  "username" varchar(60) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "is_deleted" bool NOT NULL DEFAULT false
)
;
COMMENT ON COLUMN "public"."t_comment"."id" IS 'id';
COMMENT ON COLUMN "public"."t_comment"."content" IS '评论内容';
COMMENT ON COLUMN "public"."t_comment"."avatar" IS '头像';
COMMENT ON COLUMN "public"."t_comment"."nickname" IS '昵称';
COMMENT ON COLUMN "public"."t_comment"."router_url" IS '评论所属的路由';
COMMENT ON COLUMN "public"."t_comment"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_comment"."update_time" IS '最后一次更新时间';
COMMENT ON COLUMN "public"."t_comment"."reply_comment_id" IS '回复的评论 ID';
COMMENT ON COLUMN "public"."t_comment"."parent_comment_id" IS '父评论 ID';
COMMENT ON COLUMN "public"."t_comment"."reason" IS '原因描述';
COMMENT ON COLUMN "public"."t_comment"."status" IS '1: 待审核；2：正常；3：审核未通过;';
COMMENT ON COLUMN "public"."t_comment"."username" IS '登录用户名';
COMMENT ON COLUMN "public"."t_comment"."is_deleted" IS '是否删除';
COMMENT ON TABLE "public"."t_comment" IS '评论表';

-- ----------------------------
-- Records of t_comment
-- ----------------------------
INSERT INTO "public"."t_comment" VALUES (13, '你好666', '', '一陌千尘666', '/test/1', '2026-02-12 09:27:44.249212', '2026-02-12 09:28:39.83206', NULL, NULL, '审核通过', 2, 'origin', 'f');

-- ----------------------------
-- Table structure for t_login_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_login_log";
CREATE TABLE "public"."t_login_log" (
  "id" int8 NOT NULL DEFAULT nextval('t_login_log_id_seq'::regclass),
  "username" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "ip_address" varchar(128) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "login_location" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "browser" varchar(64) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "os" varchar(64) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "status" int2 NOT NULL DEFAULT 0,
  "message" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "user_agent" varchar(512) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" bool NOT NULL DEFAULT false
)
;
COMMENT ON COLUMN "public"."t_login_log"."id" IS '主键ID';
COMMENT ON COLUMN "public"."t_login_log"."username" IS '用户名';
COMMENT ON COLUMN "public"."t_login_log"."ip_address" IS '登录IP地址';
COMMENT ON COLUMN "public"."t_login_log"."login_location" IS '登录地点';
COMMENT ON COLUMN "public"."t_login_log"."browser" IS '浏览器类型';
COMMENT ON COLUMN "public"."t_login_log"."os" IS '操作系统';
COMMENT ON COLUMN "public"."t_login_log"."status" IS '登录状态（1-成功，0-失败，-1-锁定）';
COMMENT ON COLUMN "public"."t_login_log"."message" IS '提示消息';
COMMENT ON COLUMN "public"."t_login_log"."user_agent" IS '用户代理字符串';
COMMENT ON COLUMN "public"."t_login_log"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_login_log"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."t_login_log"."is_deleted" IS '是否删除（false-未删除，true-已删除）';
COMMENT ON TABLE "public"."t_login_log" IS '登录日志表';

-- ----------------------------
-- Records of t_login_log
-- ----------------------------
INSERT INTO "public"."t_login_log" VALUES (67, 'origin', '0:0:0:0:0:0:0:1', '本地', 'Chrome 14', 'Windows 10', 0, '用户名或密码错误', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36', '2026-02-09 17:00:38.992968', '2026-02-09 17:00:38.992968', 'f');
INSERT INTO "public"."t_login_log" VALUES (68, 'origin', '192.168.1.82', '内网IP', 'Chrome 14', 'Mac OS X', 0, '用户名或密码错误', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36', '2026-02-09 17:27:35.190332', '2026-02-09 17:27:35.190332', 'f');
INSERT INTO "public"."t_login_log" VALUES (69, 'origin', '0:0:0:0:0:0:0:1', '本地', 'Chrome 14', 'Windows 10', 1, '登录成功', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36', '2026-02-11 11:52:55.339899', '2026-02-11 11:52:55.339899', 'f');

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_role";
CREATE TABLE "public"."t_role" (
  "id" int8 NOT NULL,
  "role_name" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "role_key" varchar(32) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."t_role"."id" IS 'id';
COMMENT ON COLUMN "public"."t_role"."role_name" IS '角色名称';
COMMENT ON COLUMN "public"."t_role"."role_key" IS '角色唯一标识';
COMMENT ON TABLE "public"."t_role" IS '角色表';

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO "public"."t_role" VALUES (1, '系统管理员', 'ROLE_SYSTEM_ADMIN');
INSERT INTO "public"."t_role" VALUES (2, '组织管理员', 'ROLE_ORG_ADMIN');
INSERT INTO "public"."t_role" VALUES (3, '部门管理员', 'ROLE_PART_ADMIN');
INSERT INTO "public"."t_role" VALUES (4, '组织用户', 'ROLE_ORG_USER');
INSERT INTO "public"."t_role" VALUES (5, '普通用户', 'ROLE_USER');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user";
CREATE TABLE "public"."t_user" (
  "id" int8 NOT NULL DEFAULT nextval('t_user_id_seq'::regclass),
  "username" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(60) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "is_deleted" int2 NOT NULL DEFAULT 0,
  "nickname" varchar(50) COLLATE "pg_catalog"."default",
  "avatar" varchar(255) COLLATE "pg_catalog"."default",
  "email" varchar(255) COLLATE "pg_catalog"."default",
  "phone" varchar(11) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."t_user"."id" IS 'id';
COMMENT ON COLUMN "public"."t_user"."username" IS '登录用户名';
COMMENT ON COLUMN "public"."t_user"."password" IS '密码';
COMMENT ON COLUMN "public"."t_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_user"."update_time" IS '最后一次更新时间';
COMMENT ON COLUMN "public"."t_user"."is_deleted" IS '逻辑删除：0：未删除 1：已删除';
COMMENT ON COLUMN "public"."t_user"."nickname" IS '用户昵称';
COMMENT ON COLUMN "public"."t_user"."avatar" IS '用户头像';
COMMENT ON COLUMN "public"."t_user"."email" IS '用户邮箱';
COMMENT ON COLUMN "public"."t_user"."phone" IS '手机号';
COMMENT ON TABLE "public"."t_user" IS '用户表';

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO "public"."t_user" VALUES (6, 'test', '$2a$10$9d7IG1WK7WqS.G0MT/BSje9sXSpXqVWXmJAeUkKRd24rOqvqmRe4S', '2025-11-04 08:56:34.134152', '2025-11-05 09:45:37.330087', 0, NULL, NULL, NULL, NULL);
INSERT INTO "public"."t_user" VALUES (5, 'origin', '$2a$10$aBOF400tYUqde9s/eM3GYOTPnG51sdDET7Oxu9bK8/VF.vaJwmldO', '2025-11-03 10:02:04.28619', '2025-11-07 17:37:48.805704', 0, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for t_user_role_rel
-- ----------------------------
DROP TABLE IF EXISTS "public"."t_user_role_rel";
CREATE TABLE "public"."t_user_role_rel" (
  "id" int8 NOT NULL DEFAULT nextval('t_user_role_id_seq'::regclass),
  "user_id" int8 NOT NULL,
  "role_id" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."t_user_role_rel"."id" IS 'id';
COMMENT ON COLUMN "public"."t_user_role_rel"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."t_user_role_rel"."role_id" IS '角色ID';
COMMENT ON TABLE "public"."t_user_role_rel" IS '用户角色关联表';

-- ----------------------------
-- Records of t_user_role_rel
-- ----------------------------
INSERT INTO "public"."t_user_role_rel" VALUES (4, 5, 1);
INSERT INTO "public"."t_user_role_rel" VALUES (5, 6, 5);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."t_comment_id_seq"
OWNED BY "public"."t_comment"."id";
SELECT setval('"public"."t_comment_id_seq"', 13, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."t_login_log_id_seq"
OWNED BY "public"."t_login_log"."id";
SELECT setval('"public"."t_login_log_id_seq"', 69, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."t_user_id_seq"
OWNED BY "public"."t_user"."id";
SELECT setval('"public"."t_user_id_seq"', 8, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."t_user_role_id_seq"
OWNED BY "public"."t_user_role_rel"."id";
SELECT setval('"public"."t_user_role_id_seq"', 5, true);

-- ----------------------------
-- Indexes structure for table t_comment
-- ----------------------------
CREATE INDEX "idx_parent_comment_id" ON "public"."t_comment" USING btree (
  "parent_comment_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_reply_comment_id" ON "public"."t_comment" USING btree (
  "reply_comment_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_router_url" ON "public"."t_comment" USING btree (
  "router_url" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table t_comment
-- ----------------------------
ALTER TABLE "public"."t_comment" ADD CONSTRAINT "t_comment_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table t_login_log
-- ----------------------------
CREATE INDEX "idx_create_time" ON "public"."t_login_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_ip_address" ON "public"."t_login_log" USING btree (
  "ip_address" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_status" ON "public"."t_login_log" USING btree (
  "status" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_username" ON "public"."t_login_log" USING btree (
  "username" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table t_login_log
-- ----------------------------
ALTER TABLE "public"."t_login_log" ADD CONSTRAINT "t_login_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table t_role
-- ----------------------------
ALTER TABLE "public"."t_role" ADD CONSTRAINT "t_role_role_key_key" UNIQUE ("role_key");

-- ----------------------------
-- Primary Key structure for table t_role
-- ----------------------------
ALTER TABLE "public"."t_role" ADD CONSTRAINT "t_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table t_user
-- ----------------------------
ALTER TABLE "public"."t_user" ADD CONSTRAINT "t_user_username_key" UNIQUE ("username");

-- ----------------------------
-- Primary Key structure for table t_user
-- ----------------------------
ALTER TABLE "public"."t_user" ADD CONSTRAINT "t_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table t_user_role_rel
-- ----------------------------
ALTER TABLE "public"."t_user_role_rel" ADD CONSTRAINT "t_user_role_pkey" PRIMARY KEY ("id");
