---
status: accepted
supersedes-partially: 0005-spring-cloud-nacos.md, 0008-version-matrix.md
---

# Kubernetes 为 OLTP 运行时目标；移除 Nacos 与 Spring Cloud Alibaba

[ADR-0005](0005-spring-cloud-nacos.md) 与 [ADR-0008](0008-version-matrix.md) 将 Nacos 作为注册中心与配置中心，Spring Cloud Alibaba（SCA）作为 Nacos 客户端来源。MiniMart 的**运行时目标**是 Kubernetes：服务发现由 **K8s Service DNS / Endpoints** 承担，配置由 **ConfigMap + Secret**（Helm values）承担，与 Nacos 职责重复。

因此：

- **Kubernetes** 是 OLTP 五进程（gateway + 四个业务服务）的**唯一运行时目标**（本地 kind/k3d + 同一套 Helm 为 P2/P3）。
- **删除** Spring Cloud Alibaba 整栈：`spring-cloud-starter-alibaba-nacos-discovery`、`spring-cloud-starter-alibaba-nacos-config`、BOM 中的 `spring-cloud-alibaba-dependencies`、`docker/nacos/`、compose 中的 Nacos 与 `nacos-config` 初始化。
- **保留** Spring Cloud **Gateway**（应用层入口：JWT/CORS/关联 ID/路由）、**OpenFeign** + infra 发布的 `*-api` 合同、**minimart-bom**（Boot + Cloud，无 SCA）、**五进程 + 四库**（[ADR-0006](0006-payment-own-process.md)、[ADR-0007](0007-one-repo-per-process.md)）、协作拓扑表（[ARCHITECTURE.md](../ARCHITECTURE.md)）。
- Gateway 路由与 Feign 目标改为 **显式 HTTP URL**（K8s Service 短名，如 `http://member-service:8081`），不再 `lb://` + 注册中心。
- 本地 **Docker Compose** 过渡为五服务 + 固定 Docker 网络 DNS（服务名与 K8s Service 一致），**无 Nacos**。`charts/minimart` 已提供同一套五进程 Helm（P2）。`k8s/kind/dev-up.sh` 只是可选入口；日常 kind 流程仍是 P3。
- Helm chart 放在 **`minimart-infra/charts/`**（见 [ADR-0011](0011-ingress-and-gateway.md) 与 Context `k8s-target-architecture.md`）。

**取代 ADR-0005 / 0008 中与 Nacos、SCA 相关的句子**（注册、配置、compose Nacos、SCA BOM、Nacos 客户端版本约束）。0005 中「多进程 + Feign + 不上 Eureka·Sentinel·Seata」、0008 中 Boot **4.0.8** + Cloud **2025.1.0** + **compatibility-verifier 打开** 仍然有效。

**不取代：** [ADR-0001](0001-oltp-independent-of-lake.md)–[0004](0004-money-as-integer-cents.md)、[0006](0006-payment-own-process.md)、[0007](0007-one-repo-per-process.md)、[0009](0009-shared-bom-and-api.md) 的领域与制品决策。
