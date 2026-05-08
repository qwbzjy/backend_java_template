-- ============================================
-- 商品模块表结构
-- 数据库: mall_system
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- ----------------------------
-- 商品分类表
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `level` tinyint NOT NULL DEFAULT 1 COMMENT '层级：1-一级，2-二级，3-三级',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(200) DEFAULT NULL COMMENT '分类图标',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- ----------------------------
-- 商品SPU表（标准产品单元）
-- ----------------------------
DROP TABLE IF EXISTS `product_spu`;
CREATE TABLE `product_spu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `brand` varchar(100) DEFAULT NULL COMMENT '品牌',
  `description` text COMMENT '商品描述',
  `main_image` varchar(500) NOT NULL COMMENT '主图URL',
  `images` text COMMENT '图片列表JSON',
  `spec_template` text COMMENT '规格模板JSON',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `stock` int NOT NULL DEFAULT 0 COMMENT '总库存',
  `sales` int NOT NULL DEFAULT 0 COMMENT '销量',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品SPU表';

-- ----------------------------
-- 商品SKU表（库存单位）
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `spu_id` bigint NOT NULL COMMENT 'SPU ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `specs` varchar(500) NOT NULL COMMENT '规格属性JSON',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `sales` int NOT NULL DEFAULT 0 COMMENT '销量',
  `image` varchar(500) DEFAULT NULL COMMENT 'SKU图片',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_spu_id` (`spu_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品SKU表';

-- ----------------------------
-- 商品库存表（独立库存表，便于分布式锁）
-- ----------------------------
DROP TABLE IF EXISTS `product_stock`;
CREATE TABLE `product_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sku_id` bigint NOT NULL COMMENT 'SKU ID',
  `stock` int NOT NULL DEFAULT 0 COMMENT '当前库存',
  `lock_stock` int NOT NULL DEFAULT 0 COMMENT '锁定库存（下单未支付）',
  `sold_stock` int NOT NULL DEFAULT 0 COMMENT '已售库存',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_id` (`sku_id`),
  KEY `idx_stock` (`stock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品库存表';

-- ----------------------------
-- 字段说明
-- ----------------------------
-- product_category:
--   - parent_id: 0表示顶级分类，支持树形结构
--   - level: 控制分类层级深度
--   - status: 禁用后子分类不影响

-- product_spu:
--   - images: JSON数组存储多张图片
--   - spec_template: JSON存储规格模板（如颜色、尺码）
--   - price: 作为默认售价，SKU可覆盖
--   - stock: 所有SKU库存之和（冗余字段，便于查询）

-- product_sku:
--   - specs: JSON格式，如 {"颜色": "红色", "尺码": "L"}
--   - price: SKU自己的售价，可能与SPU不同
--   - 一个SPU下的SKU共享规格模板

-- product_stock:
--   - lock_stock: 支付超时释放回 stock
--   - sold_stock: 支付成功后累加
--   - version: 乐观锁，用于扣减库存
--   - 使用业务层维护关联，不使用外键