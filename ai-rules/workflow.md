# Backend Development Workflow

开发必须严格按照以下顺序。

禁止跳步骤。

---

# 第一阶段：基础架构

顺序：

1. 创建 Maven 多模块结构
2. 配置父 pom.xml
3. 配置 Spring Boot
4. 配置 MySQL
5. 配置 Redis
6. 配置 MyBatis-Plus
7. 配置 Swagger/OpenAPI
8. 配置 JWT
9. 配置统一返回结构
10. 配置全局异常处理
11. 配置日志体系
12. 配置 Docker

完成后才能进入下一阶段。

---

# 第二阶段：数据库设计

开发流程：

1. 先设计表结构
2. 输出 SQL
3. 等待确认
4. 再生成 Entity
5. 再生成 Mapper
6. 再生成 Service
7. 最后生成 Controller

禁止直接生成 Controller。

---

# 第三阶段：模块开发

每个模块必须遵循：

SQL
→ Entity
→ DTO
→ VO
→ Mapper
→ Service
→ ServiceImpl
→ Controller
→ Swagger
→ Test

---

# 第四阶段：联调

必须：

1. Postman 测试
2. 参数校验
3. 错误码统一
4. 权限校验
5. 日志检查

---

# 第五阶段：部署

顺序：

1. Dockerfile
2. docker-compose
3. Nginx
4. Linux 部署
5. CI/CD
