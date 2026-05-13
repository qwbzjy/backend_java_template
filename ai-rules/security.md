# Security Rules

必须：

- 使用 Spring Security
- 使用 JWT
- 密码加密存储
- 接口权限控制
- 防止 SQL 注入

---

# 密码规则

必须：

- BCrypt
- 不允许明文密码

---

# JWT

必须：

- 设置过期时间
- 支持刷新 token

---

# 接口安全

必须：

- 参数校验
- 防重复提交
- 权限校验

防重复提交具体实现方案：使用 @RepeatSubmit 注解 + Redis 过期 key。
敏感配置使用 Jasypt 加密或环境变量，禁止硬编码。
本地开发使用 dev profile，生产使用 prod