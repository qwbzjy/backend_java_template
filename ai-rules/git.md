# Git Rules

# 提交规范：

```text
feat: 新功能
fix: 修复问题
refactor: 重构
docs: 文档
test: 测试
```
提交前必须执行 mvn clean compile 确保无编译错误，以及 mvn checkstyle:check（如果引入）。
---

# 分支规范

```text
main
dev
feature/*
fix/*
```

---

# 禁止行为

禁止：

- 直接提交 main
- 提交无法编译代码
- 提交 TODO 代码
