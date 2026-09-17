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

数据库配置：复制 `app/src/main/resources/application-dev.yaml.example` 为 `application-dev.yaml`（该文件已 gitignore），或通过环境变量 `DB_HOST`、`DB_USERNAME`、`DB_PASSWORD` 注入。
