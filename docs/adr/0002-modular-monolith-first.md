---
status: superseded
superseded-by: 0005-spring-cloud-nacos.md
---

# 先模块化单体，再拆进程

> **已被 [ADR-0005](0005-spring-cloud-nacos.md) 取代。** 仓库改为多进程 Spring Cloud（Nacos + Gateway + 三个业务服务）。下文仅保留当时的理由。

学习目标包含 Spring Cloud，但当时没有已在跑的多个服务，也还没有稳定的订单主路径。一上来按模块各起一个进程（注册中心、网关、远程调用）会把「学会拆分」和「把交易做对」绑在一起，两边都难验收。

因此当时决定：v1 **一个进程、多个模块**，模块边界当成将来的服务边界来守。该策略已放弃。
