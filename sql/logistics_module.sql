-- ============================================
-- 物流模块表结构
-- 数据库: mall_system
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- ----------------------------
-- 物流订单表
-- ----------------------------
DROP TABLE IF EXISTS `logistics_order`;
CREATE TABLE `logistics_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `logistics_no` varchar(64) NOT NULL COMMENT '物流单号（唯一）',
  `order_no` varchar(64) NOT NULL COMMENT '关联订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `logistics_status` tinyint NOT NULL DEFAULT 0 COMMENT '物流状态：0-待发货，1-已发货，2-运输中，3-待签收，4-已签收，5-拒收，6-退回中，7-已退回',
  `logistics_company` varchar(50) DEFAULT NULL COMMENT '物流公司',
  `logistics_code` varchar(20) DEFAULT NULL COMMENT '物流公司编码',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人手机号',
  `receiver_province` varchar(50) NOT NULL COMMENT '收货省份',
  `receiver_city` varchar(50) NOT NULL COMMENT '收货城市',
  `receiver_district` varchar(50) NOT NULL COMMENT '收货区县',
  `receiver_detail_address` varchar(200) NOT NULL COMMENT '收货详细地址',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `sign_time` datetime DEFAULT NULL COMMENT '签收时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_logistics_no` (`logistics_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_logistics_status` (`logistics_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流订单表';

-- ----------------------------
-- 物流轨迹表
-- ----------------------------
DROP TABLE IF EXISTS `logistics_track`;
CREATE TABLE `logistics_track` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `logistics_id` bigint NOT NULL COMMENT '物流订单ID',
  `logistics_no` varchar(64) NOT NULL COMMENT '物流单号',
  `status` tinyint NOT NULL COMMENT '状态',
  `status_desc` varchar(100) NOT NULL COMMENT '状态描述',
  `location` varchar(200) DEFAULT NULL COMMENT '位置',
  `track_time` datetime NOT NULL COMMENT '轨迹时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_logistics_id` (`logistics_id`),
  KEY `idx_track_time` (`track_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流轨迹表';

-- ----------------------------
-- 字段说明
-- ----------------------------
-- logistics_order:
--   - logistics_no: 唯一物流单号
--   - order_no: 关联订单号
--   - logistics_status: 0-待发货，1-已发货，2-运输中，3-待签收，4-已签收，5-拒收，6-退回中，7-已退回
--   - 收货信息快照（下单时锁定）

-- logistics_track:
--   - 物流轨迹按时间顺序存储
--   - 每次状态变更都记录一条轨迹

-- 使用业务层维护关联，不使用外键