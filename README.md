# MiniMart infra

单店 B2C 电商（B/S，前后端分离）的**编排与领域文档**仓，也是 `minimart-lake` 的系统源说明所在。交易模型按电商常用形态写，不按湖仓事件压扁 Order。

应用进程源码不在本仓。一进程一 Git 仓，见 [ADR-0007](docs/adr/0007-one-repo-per-process.md)。本仓发布平台 BOM 与 Feign API 制品（不是第六个进程），见 [ADR-0009](docs/adr/0009-shared-bom-and-api.md)。运行时目标为 **Kubernetes**，见 [ADR-0010](docs/adr/0010-kubernetes-runtime-target.md)。compose 的 `../minimart-*` 不依赖本仓目录名。

| 文档 | 内容 |
|------|------|
| [CONTEXT.md](CONTEXT.md) | 用语（User / SKU / Order / …） |
| [docs/V1-CONTRACT.md](docs/V1-CONTRACT.md) | v1 做与不做、状态机、验收场景 |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | 进程、协作表、BOM、K8s 配置 |
| [docs/LAKE-EVENTS.md](docs/LAKE-EVENTS.md) | 入湖 topic（尚未实现） |
| [docs/adr/](docs/adr/) | 不易改的决定 |

## 并列 clone

五个 Spring 服务和本仓必须是**兄弟目录**（不用 submodule）：

```
oytproject/
  minimart-infra/              ← 本仓（compose / 文档 / modules / charts）
  minimart-gateway/
  minimart-member-service/
  minimart-product-service/
  minimart-order-service/
  minimart-payment-service/
```

## 选型

合同见 [ADR-0008](docs/adr/0008-version-matrix.md)、[ADR-0010](docs/adr/0010-kubernetes-runtime-target.md)。各服务只 import `com.minimart:minimart-bom`。

| 项 | 版本 |
|----|------|
| JDK | 25（toolchain，由本机/CI 的 JDK 提供，不在仓库里写安装路径） |
| Gradle | 9.7.1 Wrapper，**每个服务仓一份**；本仓另有一份用于 `modules/` |
| Spring Boot | 4.0.8 |
| Spring Framework | 由 Boot BOM 管理 |
| Spring Cloud | 2025.1.0 |
| 平台制品 | `com.minimart:minimart-bom:0.1.0` 与四个 `*-api` |

发布到本机 Maven：

```bash
./gradlew publishToMavenLocal
```

并列开发时服务仓会 `includeBuild("../minimart-infra")`，不必先 publish。有 `GITHUB_TOKEN` 时 `./gradlew publish` 发到 GitHub Packages（`kamineayaka/minimart-infra`）。

## 运行（过渡 Compose）

五个 Spring 服务在 Docker 里联调，镜像用 **JRE 25**，不占用宿主机 `JAVA_HOME`。**不再部署 Nacos**；服务间通过 Docker 网络 DNS（`member-service`、`product-service` 等，与 K8s Service 名一致）互访。在本仓执行：

```bash
cp .env.example .env
docker compose up -d --build
```

Windows：`Copy-Item .env.example .env` 后同样 `docker compose up -d --build`。第一次会在镜像里跑 Gradle，需要出网拉依赖。

Compose **不会**起 MySQL / Redis / Kafka。Kafka 属于 lake；OLTP 不依赖 lake。配置在各服务 `application.yaml` 内；K8s 样例见 `k8s/config/`。

| 服务 | 仓 | 健康检查 | 公开前缀 |
|------|----|----------|----------|
| gateway | minimart-gateway | http://localhost:8080/actuator/health | `/member` `/product` `/order` `/payment` → `http://<service>:<port>` |
| member-service | minimart-member-service | http://localhost:8081/actuator/health | |
| product-service | minimart-product-service | http://localhost:8082/actuator/health | |
| order-service | minimart-order-service | http://localhost:8083/actuator/health | |
| payment-service | minimart-payment-service | http://localhost:8084/actuator/health | |

内部 Feign 路径 `/internal/v1/**` 不经 gateway。商品、订单、支付、出湖用例尚未实现。

MySQL、Redis 用**宿主机**已有实例，不进 compose。容器通过 `host.docker.internal` 访问（见 `.env.example` 的 `DB_HOST` / `REDIS_HOST`）。宿主机 MySQL 需允许该用户从 Docker 网桥访问；建库脚本在宿主机执行一次：

```bash
mysql -u root -p < docker/mysql/init.sql
```

出湖 Kafka 复用 `minimart-lake` 的集群，本仓库不单独起 broker。

本机不用 Docker、只编某一个服务：进入对应服务仓后 `./gradlew bootRun`。不要在本仓根目录找服务的 `bootRun`；本仓 `./gradlew` 只构建 BOM / API。

## Kubernetes（Helm）

Chart 在 `charts/minimart/`（Ingress → gateway，五个 Deployment/Service，ConfigMap `minimart-common`，数据库密码 Secret 占位）。**不**部署 MySQL、Nacos、Kafka、lake、Redis。本地 kind 仍是 P3；脚本在 `k8s/kind/`，没有 Docker/kind 时不必跑。

在本仓根目录：

```bash
helm template minimart charts/minimart -f charts/minimart/values-dev.yaml
helm lint charts/minimart -f charts/minimart/values-dev.yaml
```

说明见 [charts/minimart/README.md](charts/minimart/README.md)。Compose 仍是过渡本地联调，避免与集群双轨。
