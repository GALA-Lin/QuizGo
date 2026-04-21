-- =====================================================
-- QuizGo 数据库设计
-- MySQL 8.0+  |  字符集: utf8mb4
-- =====================================================

CREATE DATABASE IF NOT EXISTS quizgo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quizgo;

-- =====================================================
-- 1. 用户模块
-- =====================================================

-- 用户表
CREATE TABLE `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT   COMMENT '用户ID',
    `phone`         VARCHAR(20)     NOT NULL                  COMMENT '手机号',
    `password`      VARCHAR(128)    DEFAULT NULL              COMMENT '密码(BCrypt)',
    `nickname`      VARCHAR(50)     NOT NULL                  COMMENT '昵称',
    `avatar`        VARCHAR(255)    DEFAULT NULL              COMMENT '头像URL',
    `wechat_openid` VARCHAR(64)     DEFAULT NULL              COMMENT '微信OpenID',
    `study_goal`    VARCHAR(200)    DEFAULT NULL              COMMENT '学习目标',
    `status`        TINYINT         NOT NULL DEFAULT 1        COMMENT '状态: 1正常 0禁用',
    `login_fail_count` TINYINT      NOT NULL DEFAULT 0        COMMENT '连续登录失败次数',
    `locked_until`  DATETIME        DEFAULT NULL              COMMENT '账号锁定截止时间',
    `role`          TINYINT         NOT NULL DEFAULT 0        COMMENT '角色: 0普通用户 1编辑 2管理员',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_wechat_openid` (`wechat_openid`)
) COMMENT '用户表';

-- =====================================================
-- 2. 题库模块
-- =====================================================

-- 分类表（支持多级，parent_id=0 表示顶级）
CREATE TABLE `category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0       COMMENT '父分类ID，0为顶级',
    `name`        VARCHAR(50)  NOT NULL                 COMMENT '分类名称',
    `level`       TINYINT      NOT NULL DEFAULT 1       COMMENT '层级: 1学科 2章节 3知识点',
    `sort`        INT          NOT NULL DEFAULT 0       COMMENT '排序',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) COMMENT '题目分类表（多级）';

-- 题目表
CREATE TABLE `question` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT   COMMENT '题目ID',
    `category_id`   BIGINT          NOT NULL                  COMMENT '所属分类ID（叶子节点）',
    `type`          TINYINT         NOT NULL                  COMMENT '题型: 1单选 2多选 3判断 4填空 5简答',
    `difficulty`    TINYINT         NOT NULL DEFAULT 2        COMMENT '难度: 1简单 2中等 3困难',
    `content`       TEXT            NOT NULL                  COMMENT '题目内容（支持HTML富文本）',
    `options`       JSON            DEFAULT NULL              COMMENT '选项列表（客观题），格式:[{key,content}]',
    `answer`        TEXT            NOT NULL                  COMMENT '正确答案（客观题存选项key，多选逗号分隔）',
    `analysis`      TEXT            DEFAULT NULL              COMMENT '解析说明',
    `tags`          VARCHAR(255)    DEFAULT NULL              COMMENT '标签，逗号分隔',
    `status`        TINYINT         NOT NULL DEFAULT 0        COMMENT '状态: 0待审核 1已发布 2已拒绝',
    `creator_id`    BIGINT          NOT NULL                  COMMENT '创建人ID',
    `reviewer_id`   BIGINT          DEFAULT NULL              COMMENT '审核人ID',
    `reviewed_at`   DATETIME        DEFAULT NULL              COMMENT '审核时间',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_creator_id` (`creator_id`)
) COMMENT '题目表';

-- =====================================================
-- 3. 学习模块
-- =====================================================

-- 答题记录表（每次完整练习/考试为一个session）
CREATE TABLE `practice_session` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT   COMMENT '练习会话ID',
    `user_id`       BIGINT      NOT NULL                  COMMENT '用户ID',
    `mode`          TINYINT     NOT NULL                  COMMENT '模式: 1顺序 2随机 3错题 4收藏 5模拟考试',
    `category_id`   BIGINT      DEFAULT NULL              COMMENT '练习范围分类（可为空表示全部）',
    `total`         INT         NOT NULL DEFAULT 0        COMMENT '总题数',
    `correct`       INT         NOT NULL DEFAULT 0        COMMENT '答对数',
    `duration_sec`  INT         DEFAULT NULL              COMMENT '用时（秒）',
    `finished`      TINYINT     NOT NULL DEFAULT 0        COMMENT '是否完成: 0否 1是',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `finished_at`   DATETIME    DEFAULT NULL              COMMENT '完成时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) COMMENT '练习会话表';

-- 答题明细表
CREATE TABLE `answer_record` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT   COMMENT '记录ID',
    `session_id`    BIGINT      NOT NULL                  COMMENT '会话ID',
    `user_id`       BIGINT      NOT NULL                  COMMENT '用户ID',
    `question_id`   BIGINT      NOT NULL                  COMMENT '题目ID',
    `user_answer`   TEXT        DEFAULT NULL              COMMENT '用户答案',
    `is_correct`    TINYINT     NOT NULL DEFAULT 0        COMMENT '是否正确: 0否 1是',
    `is_unsure`     TINYINT     NOT NULL DEFAULT 0        COMMENT '是否标记不确定: 0否 1是',
    `answered_at`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_user_question` (`user_id`, `question_id`)
) COMMENT '答题明细表';

-- 错题本（去重，用户每道题只保留一条）
CREATE TABLE `wrong_question` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT      NOT NULL                  COMMENT '用户ID',
    `question_id`   BIGINT      NOT NULL                  COMMENT '题目ID',
    `note`          TEXT        DEFAULT NULL              COMMENT '个人笔记',
    `removed`       TINYINT     NOT NULL DEFAULT 0        COMMENT '是否移除: 0否 1是',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question` (`user_id`, `question_id`)
) COMMENT '错题本';

-- 收藏表
CREATE TABLE `favorite_question` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT      NOT NULL                  COMMENT '用户ID',
    `question_id`   BIGINT      NOT NULL                  COMMENT '题目ID',
    `note`          TEXT        DEFAULT NULL              COMMENT '个人笔记',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question` (`user_id`, `question_id`)
) COMMENT '题目收藏表';

-- =====================================================
-- 4. 管理后台模块
-- =====================================================

-- 公告表
CREATE TABLE `announcement` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(100) NOT NULL  COMMENT '公告标题',
    `content`     TEXT         NOT NULL  COMMENT '公告内容',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0停用',
    `creator_id`  BIGINT       NOT NULL  COMMENT '创建人ID',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) COMMENT '公告表';
