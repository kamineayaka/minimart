# 架构

v1 是 **五个 Spring Boot 进程 + Nacos**，不是模块化单体。决策见 [ADR-0005](adr/0005-spring-cloud-nacos.md)、[ADR-0006](adr/0006-payment-own-process.md)、[ADR-0007](adr/0007-one-repo-per-process.md)。用语见 [CONTEXT.md](../CONTEXT.md)，行为见 [V1-CONTRACT.md](V1-CONTRACT.md)。

## 进程

```
浏览器 / Apifox  →  gateway:8080
                       │  （业务路由尚未配置）
    ┌──────────┬───────┼───────────┬──────────┐
    ▼          ▼       ▼           ▼          ▼
 member:8081 product:8082 order:8083  payment:8084
 User/Address Category/SPU Cart/Order  模拟 Payment
 登录 JWT     SKU/Stock
    │          │       │           │
    └──────────┴───────┴─────┬─────┘
                             ▼
                       Nacos :8848 / :9848
```

| 服务 | 拥有 | 可以调用 | 禁止 |
|------|------|----------|------|
| gateway | 路由、JWT（尚未实现） | 经 `lb://` 转发 | 业务表 |
| member-service | User、Address、发牌 | 无 | 车、单、库存、支付 |
| product-service | Category、SPU、SKU、Stock | 无 | 订单、支付 |
| order-service | Cart、Order、OrderLine | product（库存）、member（抄地址）、payment（开单收钱） | 直接改 Stock 表；自己把单改成 PAID |
| payment-service | Payment | 无（成功后通知 order） | 改 Order 表、改 Stock 表 |

每服务将来一个 MySQL 库：`minimart_member` / `minimart_product` / `minimart_order` / `minimart_payment`。库在**宿主机 MySQL** 上，建库脚本见 `docker/mysql/init.sql`（手工执行）。Redis 同样用裸机。应用尚未连接。Kafka 不在本 compose 中，出湖时对接 `minimart-lake` 的集群。不上 Seata，见 [ADR-0006](adr/0006-payment-own-process.md)。

## 版本与 BOM

导入顺序必须是：

1. Spring Boot **4.1.1**
2. Spring Cloud Alibaba **2025.1.0.0**（只要 Nacos starter）
3. Spring Cloud **2025.1.3**（最后，避免 SCA 把 Cloud 拉回 2025.1.0）

Framework **7.0.9** 由 Boot BOM 管理，不要手写覆盖。Java **25**，Gradle Wrapper **9.7.1**。

本地配置：`application.yaml` + `spring.config.import: optional:nacos:...`。没有 `bootstrap.yml`。namespace ID 为 `dev`，group 为 `MINIMART`。

部署默认走 Docker：compose 在 **minimart-infra**，构建上下文是兄弟仓（`build: ../minimart-product-service`），镜像为 JRE 25。容器内 `NACOS_ADDR=nacos:8848`。宿主机 JDK 版本不影响容器。

## 独立构建

一进程一 Git 仓，见 [ADR-0007](adr/0007-one-repo-per-process.md)。不要用任何一个仓的根目录 `include` 把五个服务编成一次 Gradle 构建。

| 共享（minimart-infra） | 各服务仓自有 |
|------------------------|--------------|
| `compose.yaml`、`.env.example`、`docker/`、文档、`CONTEXT.md` | `settings.gradle.kts`、`build.gradle.kts`、Wrapper、Dockerfile、源码 |

单独编译：`cd ../minimart-product-service && ./gradlew bootJar`。版本号目前各仓相同，漂移是独立的代价；以后若抽平台 BOM，应发布成制品，而不是再变回父工程。
