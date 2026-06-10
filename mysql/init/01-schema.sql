-- 蚁心安巡数据库初始化脚本
-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE yixin_patrol;

-- 组织机构表
CREATE TABLE IF NOT EXISTS `sys_organization` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '机构名称',
    `code` VARCHAR(50) NOT NULL COMMENT '机构编码',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父机构ID',
    `org_type` TINYINT NOT NULL DEFAULT 1 COMMENT '机构类型: 1-教育局 2-学校 3-部门',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织机构表';

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `org_id` BIGINT COMMENT '所属机构ID',
    `role_type` TINYINT NOT NULL DEFAULT 3 COMMENT '角色类型: 1-系统管理员 2-单位管理员 3-巡检员',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 检查模板表
CREATE TABLE IF NOT EXISTS `task_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `description` TEXT COMMENT '模板描述',
    `category` VARCHAR(50) COMMENT '检查类别',
    `check_items` JSON COMMENT '检查项目列表(JSON)',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检查模板表';

-- 任务表
CREATE TABLE IF NOT EXISTS `patrol_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `template_id` BIGINT COMMENT '模板ID',
    `org_id` BIGINT COMMENT '执行单位ID',
    `executor_id` BIGINT COMMENT '执行人ID',
    `assigner_id` BIGINT COMMENT '派发人ID',
    `priority` TINYINT DEFAULT 2 COMMENT '优先级: 1-低 2-中 3-高',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待执行 1-执行中 2-待审核 3-已完成 4-已驳回',
    `deadline` DATETIME COMMENT '截止时间',
    `start_time` DATETIME COMMENT '开始时间',
    `complete_time` DATETIME COMMENT '完成时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_executor_id` (`executor_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deadline` (`deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检任务表';

-- 任务上报记录表
CREATE TABLE IF NOT EXISTS `task_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `report_content` TEXT COMMENT '上报内容',
    `check_result` JSON COMMENT '检查结果(JSON)',
    `images` JSON COMMENT '图片列表(JSON)',
    `location` VARCHAR(255) COMMENT '检查地点',
    `report_user_id` BIGINT COMMENT '上报人ID',
    `report_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上报时间',
    `review_status` TINYINT DEFAULT 0 COMMENT '审核状态: 0-待审核 1-通过 2-驳回',
    `review_user_id` BIGINT COMMENT '审核人ID',
    `review_time` DATETIME COMMENT '审核时间',
    `review_comment` TEXT COMMENT '审核意见',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_report_user_id` (`report_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务上报记录表';

-- 整改记录表
CREATE TABLE IF NOT EXISTS `rectification_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '关联任务ID',
    `report_id` BIGINT COMMENT '关联上报记录ID',
    `problem_desc` TEXT COMMENT '问题描述',
    `rectify_content` TEXT COMMENT '整改内容',
    `rectify_images` JSON COMMENT '整改图片(JSON)',
    `rectify_user_id` BIGINT COMMENT '整改人ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待整改 1-已整改 2-已验收',
    `deadline` DATETIME COMMENT '整改期限',
    `complete_time` DATETIME COMMENT '完成时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_report_id` (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='整改记录表';
