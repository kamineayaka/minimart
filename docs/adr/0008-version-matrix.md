---
status: accepted
---

# 冻结 Spring 版本矩阵，打开 compatibility-verifier

[ADR-0005](0005-spring-cloud-nacos.md) 为了对齐 Spring Cloud Alibaba 2025.1.0.0，在 Boot **4.1.1** + Cloud **2025.1.3** 上关闭了 compatibility-verifier。那是已知缺陷，不是工作流。SCA 2025.1.0.0 官方适配的是 **Spring Boot 4.0.x** 与 **Spring Cloud 2025.1.0**。

因此 v1 版本矩阵冻结为：

| 项 | 版本 |
|----|------|
| JDK | 25 |
| Gradle Wrapper | 9.7.1（每个进程仓一份） |
| Spring Boot | **4.0.8**（4.0.x 线；不用 4.1.x，直到 SCA 发布对应 train） |
| Spring Framework | 由 Boot BOM 管理，禁止手写覆盖 |
| Spring Cloud | **2025.1.0** |
| Spring Cloud Alibaba | **2025.1.0.0**（只要 Nacos discovery / config；不上 Sentinel、Seata、Bus、Stream） |
| Nacos server | **3.1.1**（compose） |
| Nacos client | **只来自 SCA BOM**，禁止再手写更新的 nacos-client |

导入顺序（写在 [minimart-bom](../../modules/minimart-bom) 里，各应用只 import 该 BOM）：

1. Spring Boot BOM
2. Spring Cloud BOM（Boot 之后）
3. Spring Cloud Alibaba BOM（**Cloud 相关 import 中最后一个**）

`spring.cloud.compatibility-verifier.enabled` 必须为 **true**（默认即可，禁止再关掉）。

本决策取代 ADR-0005 中「Boot 4.1.1 / Cloud 2025.1.3 / 关闭 verifier」一句。Nacos 作注册与配置、Feign 作同步协作、不上 Eureka·Sentinel·Seata 仍按 0005 / [0006](0006-payment-own-process.md)。
