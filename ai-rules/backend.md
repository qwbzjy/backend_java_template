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
├── common
├── gateway
├── auth-service
├── user-service
├── product-service
├── order-service
├── payment-service
├── logistics-service
```
common 模块只应该包含公共组件，不应该有业务代码

---

# 包结构规范

```text
com.xxx.project

├── controller
├── service
├── service.impl
├── mapper
├── entity
├── dto
├── vo
├── config
├── exception
├── common
```

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

---

# 日志规范

必须：

- 使用 slf4j
- 关键操作记录日志
- 错误必须记录 stack trace

禁止：

- System.out.println
