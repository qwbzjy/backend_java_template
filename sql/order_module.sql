-- ============================================
-- 订单模块表结构
-- 数据库: mall_system
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- ----------------------------
-- 订单主表
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号（唯一）',
  `user_id` bigint NOT NULL COMMENT '用户ID（预留分库分表）',
  `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消',
  `total_amount` decimal(12,2) NOT NULL COMMENT '订单总金额',
  `discount_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `pay_amount` decimal(12,2) NOT NULL COMMENT '实付金额',
  `freight_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费金额',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人手机号',
  `receiver_province` varchar(50) NOT NULL COMMENT '收货省份',
  `receiver_city` varchar(50) NOT NULL COMMENT '收货城市',
  `receiver_district` varchar(50) NOT NULL COMMENT '收货区县',
  `receiver_detail_address` varchar(200) NOT NULL COMMENT '收货详细地址',
  `buyer_remark` varchar(500) DEFAULT NULL COMMENT '买家备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（预留分库分表）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单主表';

-- ----------------------------
-- 订单明细表
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码（快照）',
  `sku_name` varchar(200) NOT NULL COMMENT '商品名称（快照）',
  `sku_image` varchar(500) DEFAULT NULL COMMENT '商品图片（快照）',
  `specs` varchar(500) DEFAULT NULL COMMENT '规格属性（快照）',
  `price` decimal(10,2) NOT NULL COMMENT '单价（快照）',
  `quantity` int NOT NULL COMMENT '购买数量',
  `total_amount` decimal(12,2) NOT NULL COMMENT '小计金额（快照）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ----------------------------
-- 订单状态流转记录表
-- ----------------------------
DROP TABLE IF EXISTS `order_status_log`;
CREATE TABLE `order_status_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_status` tinyint NOT NULL COMMENT '状态',
  `change_type` varchar(20) NOT NULL COMMENT '变更类型：SYSTEM/USER/ADMIN',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态流转记录表';

-- ----------------------------
-- 字段说明
-- ----------------------------
-- order_info:
--   - order_no: 唯一订单号，用于防止重复提交
--   - user_id: 预留分库分表字段
--   - create_time: 预留分库分表字段
--   - pay_amount: 实付金额快照（下单时锁定）
--   - receiver_*字段: 收货地址快照（下单时锁定）
--   - 禁止实时关联商品价格，必须快照

-- order_item:
--   - sku_name/sku_image/specs/price: 商品快照，下单时保存
--   - 禁止下单后修改快照内容

-- order_status:
--   0-待付款：创建订单，等待用户支付
--   1-待发货：已支付，等待商家发货
--   2-待收货：已发货，等待用户确认收货
--   3-已完成：用户确认收货或系统自动签收
--   4-已取消：用户取消或超时取消

-- 使用业务层维护关联，不使用外键