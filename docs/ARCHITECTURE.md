# 架构

v1 是 **四个 Spring Boot 进程 + Nacos**，不是模块化单体。决策见 [ADR-0005](adr/0005-spring-cloud-nacos.md)。用语见 [CONTEXT.md](../CONTEXT.md)，行为见 [V1-CONTRACT.md](V1-CONTRACT.md)。

## 进程

```
浏览器 / Apifox  →  gateway:8080
                       │  （业务路由尚未配置）
         ┌─────────────┼─────────────┐
         ▼             ▼             ▼
   member:8081   product:8082   order:8083
   User/Address   Category/SPU   Cart/Order
   登录 JWT       SKU/Stock      模拟 Payment
         │             │             │
         └─────────────┴──────┬──────┘
                              ▼
                        Nacos :8848 / :9848
```

| 服务 | 拥有 | 可以调用 | 禁止 |
|------|------|----------|------|
| gateway | 路由、JWT（尚未实现） | 经 `lb://` 转发 | 业务表 |
| member-service | User、Address、发牌 | 无 | 车、单、库存 |
| product-service | Category、SPU、SKU、Stock | 无 | 订单 |
| order-service | Cart、Order、OrderLine、Payment | product（库存）、member（抄地址） | 直接改 Stock 表 |

每服务将来一个 MySQL 库：`minimart_member` / `minimart_product` / `minimart_order`。compose 已预建，应用尚未连接。

## 版本与 BOM

导入顺序必须是：

1. Spring Boot **4.1.1**
2. Spring Cloud Alibaba **2025.1.0.0**（只要 Nacos starter）
3. Spring Cloud **2025.1.3**（最后，避免 SCA 把 Cloud 拉回 2025.1.0）

Framework **7.0.9** 由 Boot BOM 管理，不要手写覆盖。Java **25**，Gradle Wrapper **9.7.1**。

本地配置：`application.yaml` + `spring.config.import: optional:nacos:...`。没有 `bootstrap.yml`。namespace ID 为 `dev`，group 为 `MINIMART`。
