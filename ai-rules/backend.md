# Backend Coding Rules

技术栈：

- Java 21
- Spring Boot 3
- Spring Security
- JWT
- MyBatis-Plus
- MySQL 8
- Redis
- Maven

---

# Maven 模块结构

```text
project-root
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.wb.mall
│   │   │       ├── common
│   │   │       ├── config
│   │   │       ├── security
│   │   │       ├── user
│   │   │       ├── product
│   │   │       ├── order
│   │   │       ├── payment
│   │   │       └── logistics
│   │   └── resources
│   └── test
├── pom.xml
└── README.md
```
本项目为单体 Maven 工程，所有业务模块（user、product、order、payment、logistics）直接放在 com.wb.mall 包下，按业务分包，不拆分为独立 Maven 模块。

---

# 包结构规范

```text
com.wb.mall
│
├── common                # 公共组件（全局异常、统一返回、工具类等）
├── config                # 配置类（Swagger、Redis、MyBatis-Plus等）
├── security              # Spring Security + JWT 配置
│
├── user                  # 用户模块
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── entity
│   ├── dto
│   └── vo
├── product               # 商品模块
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── entity
│   ├── dto
│   └── vo
├── order                 # 订单模块
│   └── ...
├── payment               # 支付模块
│   └── ...
└── logistics             # 物流模块
    └── ...
```
每个业务模块内部独立维护自己的 controller、service、mapper、entity、dto、vo。
跨模块调用直接通过 Service 层方法调用，禁止跨 Controller 调用。
---

# Controller 规范

必须：

- 使用 RESTful 风格
- 返回统一 Result
- 使用 Swagger 注解
- 使用参数校验
- 不写业务逻辑

禁止：

- 直接操作数据库
- 写复杂逻辑
- 返回 Map

---

# Service 规范

必须：

- 所有业务逻辑写在 Service
- 使用事务
- 抛出业务异常

---

# Entity 规范

必须：

- 使用 Lombok
- 使用 MyBatis-Plus 注解
- 包含：
  - createTime
  - updateTime
  - deleted

---

# DTO 规范

DTO：

- 仅用于接收请求参数
- 必须参数校验

VO：

- 仅用于返回数据

禁止 Entity 直接返回前端。
DTO/VO 也可以使用 @Data，但需注意 @EqualsAndHashCode 的潜在问题。
---

# 日志规范

必须：

- 使用 slf4j
- 关键操作记录日志
- 错误必须记录 stack trace

禁止：

- System.out.println

# 缓存规范
- 商品详情缓存 key = product:detail:{id}，过期 30 分钟
- 商品分类列表缓存 1 小时
- 更新商品时必须清除对应缓存