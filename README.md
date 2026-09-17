# MiniMart

单店 B2C 电商（B/S，前后端分离）。也将作为 `minimart-lake` 的系统源；**交易模型按电商常用形态写，不按湖仓当前事件压扁**。

Gradle 多模块单体：`app` 负责启动与配置，`modules/*` 为业务边界。

| 文档 | 内容 |
|------|------|
| [CONTEXT.md](CONTEXT.md) | 用语（User / SKU / Order / …） |
| [docs/V1-CONTRACT.md](docs/V1-CONTRACT.md) | v1 做与不做、状态机、不变量、验收场景 |
| [docs/MODULES.md](docs/MODULES.md) | 模块所有权与依赖方向 |
| [docs/adr/](docs/adr/) | 不易改的决定 |

## 运行

```bash
./gradlew :app:bootRun
```

健康检查：`http://localhost:8080/actuator/health`

配置 profile（`application.yaml` 默认 `dev`）：

| Profile | 用途 |
|---------|------|
| `dev` | 连 MySQL + Flyway；通过环境变量 `DB_HOST`、`DB_USERNAME`、`DB_PASSWORD` 注入密码 |
| `local` | 不连库，仅起 Web；`--args="--spring.profiles.active=local"` |

```powershell
$env:DB_HOST="虚拟机IP"; $env:DB_PASSWORD="密码"
.\gradlew.bat :app:bootRun

.\gradlew.bat :app:bootRun --args="--spring.profiles.active=local"
```
