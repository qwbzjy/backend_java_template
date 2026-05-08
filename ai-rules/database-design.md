# Database Design Document

本文件定义：

- 当前项目数据库表结构
- 字段设计
- 索引设计
- 状态枚举
- 模块数据库关系

项目：

- 电商后端系统
- Spring Boot + MySQL + Redis
- 多模块 Maven 架构

模块：

- 用户模块
- 商品模块
- 订单模块
- 支付模块
- 物流模块

---

# 数据库信息

数据库名：

```text
mall_system
```

字符集：

```text
utf8mb4
```

存储引擎：

```text
InnoDB
```

---

# 通用字段

所有表必须包含：

```sql
id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
```

---

# 用户模块

---

## user_info

用户信息表

```sql
CREATE TABLE user_info (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    username VARCHAR(50) NOT NULL COMMENT '用户名',

    password VARCHAR(255) NOT NULL COMMENT '密码',

    nickname VARCHAR(50) DEFAULT '' COMMENT '昵称',

    avatar VARCHAR(500) DEFAULT '' COMMENT '头像',

    phone VARCHAR(20) DEFAULT '' COMMENT '手机号',

    email VARCHAR(100) DEFAULT '' COMMENT '邮箱',

    gender TINYINT DEFAULT 0 COMMENT '性别',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',

    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',

    UNIQUE KEY uk_username (username),

    UNIQUE KEY uk_phone (phone),

    KEY idx_status (status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';
```

---

## user_address

用户收货地址表

```sql
CREATE TABLE user_address (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    user_id BIGINT NOT NULL COMMENT '用户ID',

    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',

    receiver_phone VARCHAR(20) NOT NULL COMMENT '手机号',

    province VARCHAR(50) NOT NULL COMMENT '省',

    city VARCHAR(50) NOT NULL COMMENT '市',

    district VARCHAR(50) NOT NULL COMMENT '区',

    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',

    postal_code VARCHAR(20) DEFAULT '' COMMENT '邮编',

    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '默认地址',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    KEY idx_user_id (user_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';
```

---

# 商品模块

---

## product_category

商品分类表

```sql
CREATE TABLE product_category (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID',

    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',

    category_icon VARCHAR(500) DEFAULT '' COMMENT '分类图标',

    sort INT NOT NULL DEFAULT 0 COMMENT '排序',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
```

---

## product_spu

商品SPU表

```sql
CREATE TABLE product_spu (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',

    category_id BIGINT NOT NULL COMMENT '分类ID',

    brand_name VARCHAR(100) DEFAULT '' COMMENT '品牌名称',

    main_image VARCHAR(500) DEFAULT '' COMMENT '主图',

    description TEXT COMMENT '商品描述',

    sale_status TINYINT NOT NULL DEFAULT 1 COMMENT '销售状态',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    KEY idx_category_id (category_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU表';
```

---

## product_sku

商品SKU表

```sql
CREATE TABLE product_sku (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    spu_id BIGINT NOT NULL COMMENT 'SPU ID',

    sku_code VARCHAR(100) NOT NULL COMMENT 'SKU编码',

    sku_name VARCHAR(200) NOT NULL COMMENT 'SKU名称',

    price DECIMAL(10,2) NOT NULL COMMENT '销售价格',

    original_price DECIMAL(10,2) NOT NULL COMMENT '原价',

    stock INT NOT NULL DEFAULT 0 COMMENT '库存',

    sale_status TINYINT NOT NULL DEFAULT 1 COMMENT '销售状态',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    UNIQUE KEY uk_sku_code (sku_code),

    KEY idx_spu_id (spu_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';
```

---

# 订单模块

---

## order_info

订单主表

```sql
CREATE TABLE order_info (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    order_no VARCHAR(64) NOT NULL COMMENT '订单号',

    user_id BIGINT NOT NULL COMMENT '用户ID',

    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',

    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',

    order_status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态',

    payment_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态',

    logistics_status TINYINT NOT NULL DEFAULT 0 COMMENT '物流状态',

    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',

    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货手机号',

    receiver_address VARCHAR(255) NOT NULL COMMENT '收货地址',

    remark VARCHAR(500) DEFAULT '' COMMENT '订单备注',

    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',

    delivery_time DATETIME DEFAULT NULL COMMENT '发货时间',

    finish_time DATETIME DEFAULT NULL COMMENT '完成时间',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    UNIQUE KEY uk_order_no (order_no),

    KEY idx_user_id (user_id),

    KEY idx_order_status (order_status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';
```

---

## order_item

订单商品明细表

```sql
CREATE TABLE order_item (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    order_id BIGINT NOT NULL COMMENT '订单ID',

    sku_id BIGINT NOT NULL COMMENT 'SKU ID',

    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',

    sku_name VARCHAR(200) NOT NULL COMMENT 'SKU名称',

    product_image VARCHAR(500) DEFAULT '' COMMENT '商品图片',

    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',

    quantity INT NOT NULL COMMENT '购买数量',

    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    KEY idx_order_id (order_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';
```

---

# 支付模块

---

## payment_order

支付订单表

```sql
CREATE TABLE payment_order (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    payment_no VARCHAR(64) NOT NULL COMMENT '支付单号',

    order_no VARCHAR(64) NOT NULL COMMENT '订单号',

    user_id BIGINT NOT NULL COMMENT '用户ID',

    pay_type TINYINT NOT NULL COMMENT '支付方式',

    pay_amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',

    pay_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态',

    transaction_id VARCHAR(100) DEFAULT '' COMMENT '第三方流水号',

    callback_content TEXT COMMENT '支付回调内容',

    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    UNIQUE KEY uk_payment_no (payment_no),

    KEY idx_order_no (order_no)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';
```

---

## payment_refund

退款表

```sql
CREATE TABLE payment_refund (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    refund_no VARCHAR(64) NOT NULL COMMENT '退款单号',

    payment_no VARCHAR(64) NOT NULL COMMENT '支付单号',

    order_no VARCHAR(64) NOT NULL COMMENT '订单号',

    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',

    refund_status TINYINT NOT NULL DEFAULT 0 COMMENT '退款状态',

    refund_reason VARCHAR(255) DEFAULT '' COMMENT '退款原因',

    refund_time DATETIME DEFAULT NULL COMMENT '退款时间',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    UNIQUE KEY uk_refund_no (refund_no)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';
```

---

# 物流模块

---

## logistics_order

物流订单表

```sql
CREATE TABLE logistics_order (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    order_no VARCHAR(64) NOT NULL COMMENT '订单号',

    logistics_no VARCHAR(64) NOT NULL COMMENT '物流单号',

    company_name VARCHAR(100) NOT NULL COMMENT '物流公司',

    logistics_status TINYINT NOT NULL DEFAULT 0 COMMENT '物流状态',

    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人',

    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货电话',

    receiver_address VARCHAR(255) NOT NULL COMMENT '收货地址',

    delivery_time DATETIME DEFAULT NULL COMMENT '发货时间',

    receive_time DATETIME DEFAULT NULL COMMENT '收货时间',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    UNIQUE KEY uk_logistics_no (logistics_no),

    KEY idx_order_no (order_no)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流订单表';
```

---

## logistics_track

物流轨迹表

```sql
CREATE TABLE logistics_track (

    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',

    logistics_no VARCHAR(64) NOT NULL COMMENT '物流单号',

    track_content VARCHAR(500) NOT NULL COMMENT '物流轨迹内容',

    track_time DATETIME NOT NULL COMMENT '轨迹时间',

    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP,

    deleted TINYINT NOT NULL DEFAULT 0,

    KEY idx_logistics_no (logistics_no)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';
```

---

# 状态枚举

---

## 用户状态

```text
0 -> 禁用
1 -> 正常
```

---

## 商品状态

```text
0 -> 下架
1 -> 上架
```

---

## 订单状态

```text
0 -> 待支付
1 -> 待发货
2 -> 已发货
3 -> 已完成
4 -> 已取消
```

---

## 支付状态

```text
0 -> 未支付
1 -> 已支付
2 -> 已退款
```

---

## 退款状态

```text
0 -> 退款中
1 -> 退款成功
2 -> 退款失败
```

---

## 物流状态

```text
0 -> 待发货
1 -> 已发货
2 -> 运输中
3 -> 已签收
```

---

# 数据库开发流程

AI开发数据库时必须：

1. 先输出SQL设计
2. 输出字段说明
3. 输出索引设计
4. 等待确认
5. 再生成Entity
6. 再生成Mapper
7. 再生成Service
8. 最后生成Controller

禁止：

- 跳步骤
- 擅自修改字段
- 擅自删除字段
- 使用外键
- 直接生成全部代码
