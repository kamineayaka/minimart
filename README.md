# MiniMart

单店 B2C 电商（B/S，前后端分离），也是 `minimart-lake` 的系统源。交易模型按电商常用形态写，不按湖仓事件压扁 Order。

当前是 **Spring Cloud 多进程骨架**：能编译、能注册 Nacos。商品、订单、出湖尚未实现。

| 文档 | 内容 |
|------|------|
| [CONTEXT.md](CONTEXT.md) | 用语（User / SKU / Order / …） |
| [docs/V1-CONTRACT.md](docs/V1-CONTRACT.md) | v1 做与不做、状态机、验收场景 |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | 进程、依赖方向、BOM |
| [docs/LAKE-EVENTS.md](docs/LAKE-EVENTS.md) | 入湖 topic（尚未实现） |
| [docs/adr/](docs/adr/) | 不易改的决定 |

## 选型

| 项 | 版本 |
|----|------|
| JDK | 25（toolchain，由本机/CI 的 JDK 提供，不在仓库里写安装路径） |
| Gradle | 9.7.1 Wrapper（腾讯云镜像）。不要用 PATH 里的系统 `gradle` |
| Spring Boot | 4.1.1 |
| Spring Framework | 7.0.9（Boot BOM） |
| Spring Cloud | 2025.1.3 Oakwood |
| Nacos | Server 3.1.1；客户端 SCA 2025.1.0.0 |

## 运行

必须用仓库里的 Wrapper：

```powershell
.\gradlew.bat build
```

基础设施：

```powershell
docker compose up -d nacos
```

控制台：<http://127.0.0.1:8848/nacos>（若开启登录，默认 `nacos` / `nacos`）。

在控制台创建一个命名空间，**ID 填 `dev`**（与 `NACOS_NS` 一致，不是显示名）。Group 使用 `MINIMART`。

然后起服务（各开一个终端）：

```powershell
.\gradlew.bat :product-service:bootRun
.\gradlew.bat :member-service:bootRun
.\gradlew.bat :order-service:bootRun
.\gradlew.bat :gateway:bootRun
```

| 服务 | 健康检查 |
|------|----------|
| gateway | http://localhost:8080/actuator/health |
| member-service | http://localhost:8081/actuator/health |
| product-service | http://localhost:8082/actuator/health |
| order-service | http://localhost:8083/actuator/health |

Nacos 服务列表应出现上述四个名字。骨架阶段 Gateway **没有**业务路由；下一步才做 Feign / `lb://`。

MySQL / Redis / Kafka 一并拉起（应用暂不连接）：

```powershell
docker compose up -d
```

复制 [.env.example](.env.example) 为 `.env` 可覆盖默认密码与 `NACOS_ADDR`。
