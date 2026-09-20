---
status: accepted
---

# Payment 独立进程，不上 Seata

模拟收钱若仍放在 order-service，Payment 与 Order 共用一个库、一次本地事务就能把单打成 `PAID`。学习目标是按服务部署，且 Payment 的所有权与 Cart/Order 不同：它是针对某一笔 Order 的收钱记录，不是订单本身。

因此：增加 **payment-service**（端口 8084，库 `minimart_payment`）。order-service 仍拥有 Cart、Order、OrderLine 以及 `PENDING_PAY` → `PAID`；只有 Payment 变为 `SUCCEEDED`，Order 才能变为 `PAID`。payment-service 不直接改 Stock；确认扣减仍由 order 调 product-service。入湖仍在 Order 变为 `PAID` 时由 order-service 写 outbox。

**不上 Seata。** 模拟支付落地时若已跨库，先走幂等通知（payment 先落 `SUCCEEDED`，再通知 order 迁状态）。只有以后明确要求「支付成功与订单变 PAID 必须同一全局事务」再开 ADR 引入 Seata。

本决策取代 [ADR-0005](0005-spring-cloud-nacos.md) 中「模拟 Payment 留在 order-service」一句。Nacos / BOM / 不上 Eureka·Sentinel 仍按 0005。
