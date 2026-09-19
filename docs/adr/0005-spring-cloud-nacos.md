---
status: accepted
---

# 多进程 Spring Cloud，注册与配置用 Nacos

学习目标是尽快上手 Spring Cloud，并作为 `minimart-lake` 的交易系统源。模块化单体（[ADR-0002](0002-modular-monolith-first.md)）把「拆进程」推迟到订单主路径稳定之后；现改为一开始就按服务部署。

因此：

- 进程为 **gateway + member-service + product-service + order-service**。Cart 与模拟 Payment 留在 order-service，Stock 留在 product-service。
- 注册中心与配置中心用 **Nacos 3.1.x**，经 Spring Cloud Alibaba **仅引入** `nacos-discovery` / `nacos-config`。不上 Eureka、Sentinel、Seata。
- Spring Boot **4.1.1**、Spring Cloud **2025.1.3**（Oakwood）。SCA BOM **2025.1.0.0** 官方对齐 Boot 4.0，故 Gradle 中 Cloud BOM 后导入，并关闭 compatibility-verifier。
- 交易模型仍按 [ADR-0001](0001-oltp-independent-of-lake.md) / [ADR-0003](0003-reserve-stock-on-place.md) / [ADR-0004](0004-money-as-integer-cents.md)；出湖契约见 [LAKE-EVENTS.md](../LAKE-EVENTS.md)（尚未实现）。
