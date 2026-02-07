-- ============================================
-- GPFWZS Database Schema (Invite Code Version)
-- ============================================

-- Drop existing tables in correct order (respect foreign keys)
DROP TABLE IF EXISTS conversations;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS whitelist;
DROP TABLE IF EXISTS invite_codes;

-- ============================================
-- 1. Invite Codes Table
-- ============================================
CREATE TABLE invite_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Invite code ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT 'Invite code (unique)',
    remark VARCHAR(255) COMMENT 'Remark or description',
    status VARCHAR(20) NOT NULL DEFAULT 'UNUSED' COMMENT 'Status: UNUSED, USED',
    used_by_open_id VARCHAR(100) COMMENT 'OpenID of the user who used this code',
    used_at TIMESTAMP NULL COMMENT 'Time when the code was used',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX idx_code (code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Invite codes table';

-- ============================================
-- 2. Users Table (Modified for Huawei OAuth)
-- ============================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'User ID',
    open_id VARCHAR(100) NOT NULL UNIQUE COMMENT 'Huawei OpenID (unique identifier)',
    union_id VARCHAR(100) COMMENT 'Huawei UnionID',
    display_name VARCHAR(100) COMMENT 'Display name from Huawei',
    avatar VARCHAR(255) COMMENT 'Avatar URL from Huawei',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX idx_open_id (open_id),
    INDEX idx_union_id (union_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Users table';

-- ============================================
-- 3. Whitelist Table (Modified for OpenID)
-- ============================================
CREATE TABLE whitelist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Whitelist ID',
    open_id VARCHAR(100) NOT NULL UNIQUE COMMENT 'Whitelisted OpenID',
    description VARCHAR(255) COMMENT 'Description',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    INDEX idx_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Whitelist table';

-- ============================================
-- 4. Conversations Table
-- ============================================
CREATE TABLE conversations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Record ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    conversation_id VARCHAR(100) NOT NULL COMMENT 'Dify conversation ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX idx_user_id (user_id),
    INDEX idx_conversation_id (conversation_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Conversations table';

-- ============================================
-- Test Data (Development Only)
-- ============================================

-- Insert sample invite codes
INSERT INTO invite_codes (code, remark, status) VALUES 
('WELCOME2024', 'Initial launch code', 'UNUSED'),
('FRIEND001', 'Friend referral code 1', 'UNUSED'),
('FRIEND002', 'Friend referral code 2', 'UNUSED'),
('BETA2024', 'Beta tester code', 'UNUSED');

-- Insert test whitelist data
INSERT INTO whitelist (open_id, description) VALUES 
('TEST_OPEN_ID_001', 'Test user 1 - Direct whitelist'),
('TEST_OPEN_ID_002', 'Test user 2 - Direct whitelist');
