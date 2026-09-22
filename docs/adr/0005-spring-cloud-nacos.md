---
status: accepted
---

# 多进程 Spring Cloud，注册与配置用 Nacos

学习目标是尽快上手 Spring Cloud，并作为 `minimart-lake` 的交易系统源。模块化单体（[ADR-0002](0002-modular-monolith-first.md)）把「拆进程」推迟到订单主路径稳定之后；现改为一开始就按服务部署。

因此：

- 进程为 **gateway + member-service + product-service + order-service**（后增 payment-service，见 [ADR-0006](0006-payment-own-process.md)）。Cart 留在 order-service，Stock 留在 product-service。**「模拟 Payment 留在 order-service」已被 0006 取代。**
- 同一仓库，但每个服务是独立 Gradle 工程（自己的 Wrapper / BOM / Dockerfile），构建上下文互不包含对方源码。运行时只通过 Nacos / Feign 协作。根目录只保留 compose 与文档。**「同一仓库」已被 [ADR-0007](0007-one-repo-per-process.md) 取代。**
- 注册中心与配置中心用 **Nacos 3.1.x**，经 Spring Cloud Alibaba **仅引入** `nacos-discovery` / `nacos-config`。不上 Eureka、Sentinel、Seata。**注册与配置已被 [ADR-0010](0010-kubernetes-runtime-target.md) 取代（K8s + ConfigMap/Secret，移除 SCA）。**
- Spring Boot **4.1.1**、Spring Cloud **2025.1.3**（Oakwood）。SCA BOM **2025.1.0.0** 官方对齐 Boot 4.0，故 Gradle 中 Cloud BOM 后导入，并关闭 compatibility-verifier。**版本矩阵与 verifier 已被 [ADR-0008](0008-version-matrix.md) 取代。**
- 交易模型仍按 [ADR-0001](0001-oltp-independent-of-lake.md) / [ADR-0003](0003-reserve-stock-on-place.md) / [ADR-0004](0004-money-as-integer-cents.md)；出湖契约见 [LAKE-EVENTS.md](../LAKE-EVENTS.md)（尚未实现）。
