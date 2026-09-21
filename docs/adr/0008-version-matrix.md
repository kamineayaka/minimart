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
| Spring Boot | **4.0.8** |
| Spring Framework | 由 Boot BOM 管理，禁止手写覆盖 |
| Spring Cloud | **2025.1.0** |

> **Nacos / Spring Cloud Alibaba 行已被 [ADR-0010](0010-kubernetes-runtime-target.md) 移除。**

导入顺序（写在 [minimart-bom](../../modules/minimart-bom) 里，各应用只 import 该 BOM）：

1. Spring Boot BOM
2. Spring Cloud BOM（Boot 之后）

`spring.cloud.compatibility-verifier.enabled` 必须为 **true**（默认即可，禁止再关掉）。

本决策取代 ADR-0005 中「Boot 4.1.1 / Cloud 2025.1.3 / 关闭 verifier」一句。Feign 作同步协作、不上 Eureka·Sentinel·Seata 仍按 0005 / [0006](0006-payment-own-process.md)。**注册与配置中心**改按 [ADR-0010](0010-kubernetes-runtime-target.md)（K8s Service DNS + ConfigMap/Secret）。
