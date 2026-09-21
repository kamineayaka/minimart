---
status: accepted
---

# 平台 BOM 与 Feign API 从 infra 发布，而不是再做一个进程仓

五个 Spring 仓各自复制 Boot / Cloud / SCA 版本，Feign 接口若只写在调用方，契约会漂。infra 已经是文档源；运行时契约需要**已发布的制品**。[ADR-0007](0007-one-repo-per-process.md) 禁止把五个服务编成一次 Gradle 构建，也禁止再变回父工程。

因此：在 **minimart-infra** 增加 `modules/` 制品（不是第六个运行进程，也不另开 GitHub 组织级 process 仓）：

| 制品 | 坐标 | 内容 |
|------|------|------|
| 平台 BOM | `com.minimart:minimart-bom:0.1.0` | 只冻结 Boot / Cloud / 测试库版本，见 [ADR-0008](0008-version-matrix.md) |
| `minimart-api-common` | 同上 group | 错误体、幂等头、关联 ID、Feign 安全重试 / ErrorDecoder |
| `minimart-member-api` 等 | 四个小 jar | Feign 接口与协作 DTO，匹配 [ARCHITECTURE.md](../ARCHITECTURE.md) 拓扑表 |

规则：

- 无 Spring Boot 应用、无 JDBC、无 Flyway、无领域表。实体与迁移留在各服务仓。
- Gateway 与四个服务 **依赖 BOM + 各自需要的 API**。禁止只在调用方定义 Feign 接口。
- 本地并列 clone：服务仓 `includeBuild("../minimart-infra")`。Compose 构建把 infra 作为 named context。发布：`./gradlew publishToMavenLocal`（有 `GITHUB_TOKEN` 时可发 GitHub Packages）。
- BOM 不是五个服务的 Gradle parent。

本决策不合并服务、不引入 Seata / Bus / Stream / Sentinel。
