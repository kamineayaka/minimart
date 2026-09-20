# MiniMart infra

单店 B2C 电商（B/S，前后端分离）的**编排与领域文档**仓，也是 `minimart-lake` 的系统源说明所在。交易模型按电商常用形态写，不按湖仓事件压扁 Order。

应用源码不在本仓。一进程一 Git 仓，见 [ADR-0007](docs/adr/0007-one-repo-per-process.md)。本仓即 `minimart-infra`。若工作区目录仍叫 `minimart-1`，把它改名为 `minimart-infra`（IDE 打开本仓时可能改不了名）。compose 的 `../minimart-*` 不依赖本仓目录名。

| 文档 | 内容 |
|------|------|
| [CONTEXT.md](CONTEXT.md) | 用语（User / SKU / Order / …） |
| [docs/V1-CONTRACT.md](docs/V1-CONTRACT.md) | v1 做与不做、状态机、验收场景 |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | 进程、依赖方向、BOM |
| [docs/LAKE-EVENTS.md](docs/LAKE-EVENTS.md) | 入湖 topic（尚未实现） |
| [docs/adr/](docs/adr/) | 不易改的决定 |

## 并列 clone

五个 Spring 服务和本仓必须是**兄弟目录**（不用 submodule）：

```
oytproject/
  minimart-infra/              ← 本仓（compose / 文档）
  minimart-gateway/
  minimart-member-service/
  minimart-product-service/
  minimart-order-service/
  minimart-payment-service/
```

## 选型

| 项 | 版本 |
|----|------|
| JDK | 25（toolchain，由本机/CI 的 JDK 提供，不在仓库里写安装路径） |
| Gradle | 9.7.1 Wrapper，**每个服务仓一份**。不要用 PATH 里的系统 `gradle` |
| Spring Boot | 4.1.1 |
| Spring Framework | 7.0.9（Boot BOM） |
| Spring Cloud | 2025.1.3 Oakwood |
| Nacos | Server 3.1.1；客户端 SCA 2025.1.0.0 |

## 运行

五个 Spring 服务和 Nacos 都在 Docker 里，镜像用 **JRE 25**，不占用宿主机 `JAVA_HOME`（其它业务可继续用 JDK 17）。在本仓执行：

```bash
cp .env.example .env
docker compose up -d --build
```

Windows：`Copy-Item .env.example .env` 后同样 `docker compose up -d --build`。第一次会在镜像里跑 Gradle，需要出网拉依赖。Nacos 3.x 即使关闭登录也必须有 Base64 的 `NACOS_AUTH_TOKEN`（`.env.example` 里有开发占位）。

控制台（Nacos 3.x UI）：<http://127.0.0.1:18080>（服务器用 `http://<IP>:18080`）。8848 是 OpenAPI，根路径 404 是正常的。若开启登录，默认 `nacos` / `nacos`。防火墙放行 **18080** 和 **8080**。

在控制台创建一个命名空间，**ID 填 `dev`**（与 `NACOS_NS` 一致，不是显示名）。Group 使用 `MINIMART`。

| 服务 | 仓 | 健康检查 |
|------|----|----------|
| gateway | minimart-gateway | http://localhost:8080/actuator/health |
| member-service | minimart-member-service | http://localhost:8081/actuator/health |
| product-service | minimart-product-service | http://localhost:8082/actuator/health |
| order-service | minimart-order-service | http://localhost:8083/actuator/health |
| payment-service | minimart-payment-service | http://localhost:8084/actuator/health |

容器内访问 Nacos 用 `nacos:8848`，不要用 `127.0.0.1`。Nacos 服务列表（命名空间 `dev`）应出现五个名字。骨架阶段 Gateway **没有**业务路由。商品、订单、支付、出湖尚未实现。

MySQL、Redis 用**宿主机**已有实例，不进 compose。容器通过 `host.docker.internal` 访问（见 `.env.example` 的 `DB_HOST` / `REDIS_HOST`）。宿主机 MySQL 需允许该用户从 Docker 网桥访问；建库脚本在宿主机执行一次：

```bash
mysql -u root -p < docker/mysql/init.sql
```

出湖 Kafka 复用 `minimart-lake` 的集群，本仓库不单独起 broker。

本机不用 Docker、只编某一个服务：进入对应服务仓后 `./gradlew bootRun`（此时 `NACOS_ADDR=127.0.0.1:8848`，Nacos 需已在本机或 compose 里起来）。不要在本仓根目录找 `gradlew`。
