-- ============================================
-- 支付模块表结构
-- 数据库: mall_system
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- ----------------------------
-- 支付订单表
-- ----------------------------
DROP TABLE IF EXISTS `payment_order`;
CREATE TABLE `payment_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `payment_no` varchar(64) NOT NULL COMMENT '支付流水号（唯一）',
  `order_no` varchar(64) NOT NULL COMMENT '关联订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(12,2) NOT NULL COMMENT '支付金额',
  `payment_status` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败，4-已退款',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式：ALIPAY/WECHAT/BANK_CARD',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `trade_no` varchar(128) DEFAULT NULL COMMENT '第三方支付流水号',
  `callback_content` text COMMENT '回调内容JSON',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付订单表';

-- ----------------------------
-- 退款记录表
-- ----------------------------
DROP TABLE IF EXISTS `payment_refund`;
CREATE TABLE `payment_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `refund_no` varchar(64) NOT NULL COMMENT '退款流水号（唯一）',
  `payment_no` varchar(64) NOT NULL COMMENT '原支付流水号',
  `order_no` varchar(64) NOT NULL COMMENT '关联订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `refund_amount` decimal(12,2) NOT NULL COMMENT '退款金额',
  `refund_status` tinyint NOT NULL DEFAULT 0 COMMENT '退款状态：0-待处理，1-退款中，2-退款成功，3-退款失败',
  `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `refund_method` varchar(20) DEFAULT NULL COMMENT '退款方式',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `trade_no` varchar(128) DEFAULT NULL COMMENT '第三方退款流水号',
  `callback_content` text COMMENT '回调内容JSON',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_payment_no` (`payment_no`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款记录表';

-- ----------------------------
-- 字段说明
-- ----------------------------
-- payment_order:
--   - payment_no: 唯一支付流水号
--   - order_no: 关联订单号
--   - amount: 支付金额（下单时锁定）
--   - payment_status: 0-待支付，1-支付中，2-支付成功，3-支付失败，4-已退款
--   - trade_no: 第三方支付平台返回的流水号
--   - callback_content: 回调原始数据（用于幂等处理）
--   - expire_time: 支付超时时间

-- payment_refund:
--   - refund_no: 唯一退款流水号
--   - payment_no: 关联原支付流水号
--   - refund_status: 0-待处理，1-退款中，2-退款成功，3-退款失败
--   - trade_no: 第三方退款流水号

-- 支付回调幂等处理：
--   1. 根据 trade_no 判断是否已处理
--   2. 使用 Redis 分布式锁防止并发
--   3. 记录完整回调内容用于对账

-- 使用业务层维护关联，不使用外键