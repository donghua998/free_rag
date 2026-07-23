-- ========================================
-- 知识库问答系统 - 数据库初始化脚本
-- 执行前请确认已创建数据库：ai_knowledge_base
-- ========================================

-- ========================================
-- 1. 文档分块表（kb_chunk）
-- 用于存储文档分片后的文本块
-- ========================================
CREATE TABLE IF NOT EXISTS `kb_chunk` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分块ID',
    `doc_id` BIGINT NOT NULL COMMENT '所属文档ID',
    `kb_id` BIGINT NOT NULL COMMENT '所属知识库ID',
    `chunk_index` INT NOT NULL COMMENT '分块序号',
    `content` TEXT NOT NULL COMMENT '分块文本内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_doc_id` (`doc_id`),
    INDEX `idx_kb_id` (`kb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档分块表';


-- ========================================
-- 2. API Key 表（api_key）
-- 用于存储外部系统调用的 API 密钥
-- ========================================
CREATE TABLE IF NOT EXISTS `api_key` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `api_key` VARCHAR(64) NOT NULL UNIQUE COMMENT '32位随机密钥',
    `name` VARCHAR(100) NOT NULL COMMENT '名称',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0=禁用 1=启用',
    `rate_limit` INT DEFAULT 30 COMMENT '每分钟最大请求数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API Key 表';


-- ========================================
-- 3. API 调用日志表（api_log）
-- 用于记录 API 的调用记录（限流、统计用）
-- ========================================
CREATE TABLE IF NOT EXISTS `api_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `api_key_id` BIGINT NOT NULL COMMENT '关联的 API Key ID',
    `request_path` VARCHAR(255) DEFAULT NULL COMMENT '请求路径',
    `query` TEXT DEFAULT NULL COMMENT '查询内容',
    `response_time` BIGINT DEFAULT NULL COMMENT '响应耗时(ms)',
    `ip` VARCHAR(64) DEFAULT NULL COMMENT '客户端 IP',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
    INDEX `idx_api_key_id` (`api_key_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 调用日志表';
