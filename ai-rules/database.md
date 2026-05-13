# Database Rules

本文件定义：

- 数据库开发规范
- SQL设计规范
- 字段规范
- 索引规范
- AI数据库开发规则

AI开发过程中必须严格遵守。

---

# 数据库基础配置

数据库：

- MySQL 8

字符集：

- utf8mb4

排序规则：

- utf8mb4_unicode_ci

存储引擎：

- InnoDB

时区：

- Asia/Shanghai

---

# 数据库命名规范

## 数据库名

必须：

- 小写
- 下划线

示例：

```text
mall_system
```

---

# 表命名规范

必须：

- 小写
- 下划线
- 使用业务前缀

示例：

```text
user_info
user_address

product_spu
product_sku
product_category

order_info
order_item

payment_order
payment_refund

logistics_order
logistics_track
```

禁止：

- 驼峰
- 拼音
- 复数命名

---

# 字段命名规范

必须：

- 小写下划线
- 见名知意

示例：

```text
user_id
order_no
product_name
payment_status
create_time
```

禁止：

```text
name1
data
temp
flag
```

---

# 主键规范

所有表必须：

```sql
id bigint primary key auto_increment
```

禁止：

- UUID字符串主键
- 联合主键

---

# 通用字段规范

所有表必须包含：

```sql
create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

deleted tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除'
```

---

# 状态字段规范

状态字段统一：

```text
0 -> 禁用/未支付/关闭
1 -> 启用/已支付/正常
```

禁止：

```text
true/false
yes/no
```

---

# 金额字段规范

金额字段必须：

```sql
decimal(10,2)
```

禁止：

```sql
float
double
```

金额单位：

- 元

示例：

```sql
pay_amount decimal(10,2)
```

---

# 时间字段规范

必须：

```sql
datetime
```

禁止：

```sql
varchar
timestamp
```

---

# VARCHAR长度规范

推荐：

```text
手机号：varchar(20)
用户名：varchar(50)
昵称：varchar(50)
邮箱：varchar(100)
订单号：varchar(64)
URL：varchar(500)
备注：varchar(500)
```

禁止：

```sql
varchar(5000)
```

---

# TEXT字段规范

禁止：

- 使用text保存JSON
- 大量滥用text

仅允许：

- 商品详情
- 富文本内容
- 长描述

---

# 索引规范

必须：

- 高频查询字段建立索引
- 唯一字段建立唯一索引
- 外键关联字段建立索引

---

# 索引命名规范

普通索引：

```text
idx_xxx
```

唯一索引：

```text
uk_xxx
```

示例：

```sql
KEY idx_user_id (user_id)

UNIQUE KEY uk_order_no (order_no)
```

---

# 外键规范

禁止使用：

```sql
foreign key
```

所有关联关系：

- 使用业务层维护
- 使用逻辑关联

原因：

- 降低数据库耦合
- 提高扩展性
- 方便分库分表
- 提高性能

---

# 删除规范

禁止：

```sql
delete from
```

必须：

- 逻辑删除

字段：

```sql
deleted tinyint
```

MyBatis-Plus 全局配置 logic-delete-field: deleted
所有自定义 SQL 必须手动添加 AND deleted = 0
---

# 查询规范

必须：

- 查询指定字段
- 使用分页
- 查询必须带条件

禁止：

```sql
select *
```

禁止：

- 全表扫描
- 无限制分页
- 大事务

---

# 分页规范

统一：

```sql
limit ?, ?
```

分页参数：

```text
pageNum
pageSize
```

默认：

```text
pageSize <= 20
```

最大：

```text
pageSize <= 100
```

---

# SQL规范

SQL关键字：

- 大写

表名字段名：

- 小写

示例：

```sql
SELECT id, username
FROM user_info
WHERE deleted = 0
ORDER BY id DESC
```

---

# 审计字段规范

建议所有业务表包含：

```sql
create_by bigint COMMENT '创建人',

update_by bigint COMMENT '更新人'
```

---

# 乐观锁规范

推荐：

```sql
version int NOT NULL DEFAULT 0
```

用于：

- 库存扣减
- 支付状态更新
- 并发控制
所有涉及库存、金额变更的表必须包含 version 字段
---

# 库存设计规范

库存扣减必须：

- 使用乐观锁
- 或Redis预扣减

禁止：

- 直接 update stock = stock - 1

---

# 订单设计规范

订单表必须：

- 使用唯一订单号
- 保存金额快照
- 保存商品快照

禁止：

- 实时关联商品价格

---

# 支付设计规范

支付表必须：

- 保存第三方流水号
- 保存支付状态
- 保存回调信息

支付回调必须：

- 幂等处理

---

# 物流设计规范

物流表必须：

- 保存物流单号
- 保存物流状态
- 保存物流轨迹

---

# AI开发规则

AI开发数据库时必须：

1. 先设计SQL
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

---

# Entity生成规范

Entity必须：

- 使用Lombok
- 使用MyBatis-Plus注解
- 字段与数据库完全一致

禁止：

- Entity直接返回前端

---

# 数据库性能规范

必须：

- 高频字段建立索引
- 避免深分页
- 避免循环SQL
- 使用批量插入

禁止：

- N+1查询
- 长事务
- 大批量update

---

# 分库分表预留

订单相关表必须预留：

```sql
user_id
create_time
```

用于：

- 后续分库分表
- 按用户路由
- 按时间路由

---

# AI禁止行为

禁止：

- 自动修改已有SQL
- 自动删除字段
- 自动重命名字段
- 自动修改索引
- 自动改变字段类型

任何数据库变更：

必须先输出方案。
