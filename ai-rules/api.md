# API Rules

所有接口必须遵循 RESTful。

---

# 返回结构

统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

# 错误码

```text
200 成功
400 参数错误
401 未登录
403 无权限
500 系统异常
```

---

# 接口规范

必须：

- 使用 JSON
- 使用 HTTPS
- 使用 JWT

---

# Swagger

所有接口必须：

- 添加 @Operation
- 添加接口说明
- 添加参数说明

---

# 分页接口

统一格式：

```json
{
  "pageNum": 1,
  "pageSize": 10
}
```
# 版本管理
 所有接口路径增加 /v1 前缀，例如 /api/v1/user/login，方便未来升级。