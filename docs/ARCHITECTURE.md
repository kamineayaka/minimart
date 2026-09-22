# 架构

v1 是 **五个 Spring Boot 进程**，部署目标为 **Kubernetes**（见 [ADR-0010](adr/0010-kubernetes-runtime-target.md)、[ADR-0011](adr/0011-ingress-and-gateway.md)）。不是模块化单体。决策见 [ADR-0006](adr/0006-payment-own-process.md)、[ADR-0007](adr/0007-one-repo-per-process.md)、[ADR-0008](adr/0008-version-matrix.md)、[ADR-0009](adr/0009-shared-bom-and-api.md)。用语见 [CONTEXT.md](../CONTEXT.md)，行为见 [V1-CONTRACT.md](V1-CONTRACT.md)。

## 进程

```
Internet
   │
   ▼
Ingress (TLS, host)          ← 集群边，见 ADR-0011
   │
   ▼
gateway:8080                 ← JWT / CORS / correlation / 路由
   │  /member /product /order /payment  →  http://<service>:<port>
   ├──────────┬───────────┬──────────────┐
   ▼          ▼           ▼              ▼
member:8081 product:8082 order:8083  payment:8084
 User/Address Category/SPU Cart/Order  模拟 Payment
 登录 JWT     SKU/Stock
   │          │       │           │
   └──────────┴───────┴─────┬─────┘
                            ▼
              Feign（Cluster DNS，不经 gateway）
                            │
              ConfigMap / Secret（替代 Nacos）
```

Gateway 是唯一**应用层**公网入口（Ingress 只做 TLS/域名）。JWT 只在 gateway 校验（尚未实现发牌）。内部 Feign 走 `/internal/v1/**`，**不**经 gateway。Gateway 无 MySQL。CORS、关联 ID、限流只放在 gateway。

Helm chart 已在 **`charts/minimart/`**（P2 已落地：五 Deployment/Service、Ingress 只进 gateway、ConfigMap `minimart-common`、外部 MySQL 的 Secret 占位）。本地 kind 安装仍是 **P3**（`k8s/kind/dev-up.sh` 只是可选脚本）。

## 协作拓扑（实现门禁）

在写任何用例代码之前，同步调用只能是下表。Feign 接口发布在 infra 的 `*-api` 制品里，禁止只写在调用方。

| From | Sync calls（Feign + 显式 Service URL） | Must not |
|------|----------------------------------------|----------|
| gateway | 四个服务（`http://member-service:8081` 等，公开前缀 `/member` `/product` `/order` `/payment`） | 拥有业务表；转发 `/internal/**` |
| member-service | 无 | 车、单、库存、支付 |
| product-service | 无 | 订单、支付 |
| order-service | product（reserve / confirm / release）、member（抄 Address）、payment（开单收钱） | 自己写 Stock 表；自己把单改成 `PAID` |
| payment-service | order（幂等 pay-result） | 写 Order 或 Stock |

失败与重试（仍不是业务功能）：

- 超时与重试只用于 **幂等读**（GET）或已带 `Idempotency-Key` 的写。
- 支付成功是「先持久化 Payment `SUCCEEDED`，再通知 order」；order 把 `PENDING_PAY` → `PAID` 做一次。重复通知是 no-op。
- v1 **不上** Spring Cloud Bus、Stream、Sentinel、Seata。

## 出站 outbox（publisher 可后做）

Kafka 属于 `minimart-lake` 的 broker。**本仓 compose 不起 Kafka，OLTP 不依赖 lake。** Kafka 宕机时店仍能下单：order-service 在 `PAID` / `REFUNDED` 的同一事务写入 outbox 即可。

设计（尚未建表；表属于 order-service Flyway，不在本仓 `init.sql`）：

```
order_outbox(
  id, event_id UNIQUE,   -- 按 OrderLine 稳定，便于湖仓幂等
  event_type,            -- OrderLinePaid / OrderLineRefunded
  payload_json,          -- 整数分；元只在 JSON 边界转换
  created_at,
  published_at NULL      -- publisher 成功后才填
)
```

publisher 以后再接 `orders.events`。见 [LAKE-EVENTS.md](LAKE-EVENTS.md)、[ADR-0001](adr/0001-oltp-independent-of-lake.md)。

## 数据与主机约束

每服务将来一个 MySQL 库：`minimart_member` / `minimart_product` / `minimart_order` / `minimart_payment`。库在**宿主机 MySQL** 上，建库脚本见 `docker/mysql/init.sql`（手工执行，compose 不跑 MySQL）。Redis 同样用裸机。容器用 `host.docker.internal`。应用尚未连库。不上 Seata，见 [ADR-0006](adr/0006-payment-own-process.md)。

## 版本与 BOM

版本合同见 [ADR-0008](adr/0008-version-matrix.md)、[ADR-0010](adr/0010-kubernetes-runtime-target.md)。各服务 **只** import `com.minimart:minimart-bom:0.1.0`，不要再各自写 Boot/Cloud BOM。BOM 在 infra `modules/minimart-bom`，不是 Gradle parent，见 [ADR-0009](adr/0009-shared-bom-and-api.md)。

`minimart-bom` 内部导入顺序：Boot **4.0.8** → Cloud **2025.1.0**（**无** Spring Cloud Alibaba）。compatibility-verifier **打开**。

Java **25**，Gradle Wrapper **9.7.1**。Framework 由 Boot BOM 管理。

发布：在 minimart-infra 执行 `./gradlew publishToMavenLocal`。并列 clone 时服务仓 `includeBuild("../minimart-infra")`。Compose 构建通过 `additional_contexts.infra` 把本仓交给各服务 Dockerfile。

## 配置（ConfigMap / Secret，替代 Nacos）

| 内容 | 载体 |
|------|------|
| Jackson、Feign 超时、日志 pattern | ConfigMap `minimart-common`（样例：`k8s/config/minimart-common.yaml`） |
| `server.port`、datasource URL | 各应用 ConfigMap + Secret（样例：`k8s/config/<app>.yaml`） |
| Feign 目标 URL | order/payment 的 ConfigMap 或 `application.yaml` |
| 按环境 | Helm `charts/minimart/values.yaml` 与 `values-dev.yaml`（P2 已落地；P3 kind 仍在后面） |

各服务 `application.yaml` 已内嵌公共配置；`k8s/config/` 提供与旧 Nacos data-id 等价的 ConfigMap-ready 样例。本地 `./gradlew bootRun` 不依赖外部配置中心。Compose 使用同一套应用配置，Docker 网络 DNS 名与 K8s Service 名一致。

## 独立构建

一进程一 Git 仓，见 [ADR-0007](adr/0007-one-repo-per-process.md)。不要用任何一个仓的根目录 `include` 把五个服务编成一次 Gradle 构建。

| 共享（minimart-infra） | 各服务仓自有 |
|------------------------|--------------|
| `compose.yaml`、`.env.example`、`docker/`、`k8s/config/`、`charts/`、文档、`CONTEXT.md`、`modules/`（BOM + API） | `settings.gradle.kts`、`build.gradle.kts`、Wrapper、Dockerfile、源码 |

单独编译：`cd ../minimart-product-service && ./gradlew bootJar`。
