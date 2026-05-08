# Backend API Design Document

本文件定义：

- 电商系统所有后端接口设计
- 接口路径规范
- 请求参数规范
- 返回结构规范
- 权限说明
- 模块边界

用于指导 AI（Claude Code）进行后端开发。

---

# 通用约定

## 基础路径

所有接口统一前缀：

```text
/api
```

---

## 统一返回结构

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

## 分页结构

请求：

```json
{
  "pageNum": 1,
  "pageSize": 10
}
```

返回：

```json
{
  "list": [],
  "total": 0,
  "pageNum": 1,
  "pageSize": 10
}
```

---

## 权限说明

- 未标注：登录即可访问
- ADMIN：管理员
- USER：普通用户

---

# 一、用户模块 user-service

---

## 1. 用户注册

```text
POST /api/user/register
```

请求：

```json
{
  "username": "test",
  "password": "123456",
  "phone": "138xxxx"
}
```

返回：

```json
{
  "userId": 1
}
```

---

## 2. 用户登录

```text
POST /api/user/login
```

请求：

```json
{
  "username": "test",
  "password": "123456"
}
```

返回：

```json
{
  "token": "jwt-token"
}
```

---

## 3. 获取用户信息

```text
GET /api/user/info
```

返回：

```json
{
  "id": 1,
  "username": "test",
  "nickname": "xxx",
  "avatar": ""
}
```

---

## 4. 修改用户信息

```text
PUT /api/user/update
```

---

## 5. 用户地址列表

```text
GET /api/user/address/list
```

---

## 6. 新增地址

```text
POST /api/user/address/add
```

---

# 二、商品模块 product-service

---

## 1. 商品列表

```text
GET /api/product/list
```

请求：

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "categoryId": 1
}
```

---

## 2. 商品详情

```text
GET /api/product/detail/{id}
```

---

## 3. 商品分类

```text
GET /api/product/category/list
```

---

## 4. 创建商品（ADMIN）

```text
POST /api/product/create
```

---

## 5. 更新商品（ADMIN）

```text
PUT /api/product/update
```

---

## 6. 商品上下架（ADMIN）

```text
POST /api/product/status/change
```

---

# 三、订单模块 order-service

---

## 1. 创建订单

```text
POST /api/order/create
```

请求：

```json
{
  "addressId": 1,
  "items": [
    {
      "skuId": 1,
      "quantity": 2
    }
  ]
}
```

返回：

```json
{
  "orderNo": "20260001"
}
```

---

## 2. 订单详情

```text
GET /api/order/detail/{orderNo}
```

---

## 3. 订单列表

```text
GET /api/order/list
```

---

## 4. 取消订单

```text
POST /api/order/cancel/{orderNo}
```

---

## 5. 确认收货

```text
POST /api/order/confirm/{orderNo}
```

---

# 四、支付模块 payment-service

---

## 1. 发起支付

```text
POST /api/payment/pay
```

请求：

```json
{
  "orderNo": "20260001",
  "payType": 1
}
```

返回：

```json
{
  "payUrl": "xxx"
}
```

---

## 2. 支付回调（第三方）

```text
POST /api/payment/callback
```

---

## 3. 支付状态查询

```text
GET /api/payment/status/{orderNo}
```

---

## 4. 退款申请

```text
POST /api/payment/refund
```

---

# 五、物流模块 logistics-service

---

## 1. 创建物流单

```text
POST /api/logistics/create
```

---

## 2. 物流详情

```text
GET /api/logistics/detail/{orderNo}
```

---

## 3. 物流轨迹

```text
GET /api/logistics/track/{logisticsNo}
```

---

## 4. 更新物流状态

```text
POST /api/logistics/update-status
```

---

# 六、接口开发规则（AI必须遵守）

---

## 1. 禁止行为

- 禁止自创接口路径
- 禁止修改已定义路径
- 禁止不使用统一返回结构
- 禁止 Controller 写业务逻辑

---

## 2. 强制开发顺序

每个接口必须按：

```text
SQL → Entity → DTO → Mapper → Service → Controller
```

---

## 3. 接口实现要求

- 必须参数校验
- 必须异常处理
- 必须日志记录
- 必须权限控制（如需要）

---

## 4. 接口变更规则

任何修改必须：

1. 先更新 backend_api.md
2. 再修改代码
3. 禁止代码先行

---

# 七、模块边界规则

---

## user-service

只允许：

- 用户信息
- 地址管理

禁止：

- 操作订单
- 操作支付

---

## product-service

只允许：

- 商品
- 分类
- 库存

---

## order-service

只允许：

- 订单生成
- 订单状态

---

## payment-service

只允许：

- 支付
- 退款

---

## logistics-service

只允许：

- 物流单
- 物流轨迹

---

# 八、AI开发约束（最重要）

AI必须遵守：

- 所有接口必须来源于本文件
- 不允许擅自新增接口
- 不允许跳过设计直接写代码
- 每次只开发一个接口或一个小功能

---

# 九、开发流程

标准流程：

```text
1. 先查 backend_api.md
2. 设计数据库
3. 生成SQL
4. 生成代码
5. 测试接口
6. 回填文档
```
