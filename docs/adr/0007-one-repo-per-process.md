---
status: accepted
---

# 一进程一 Git 仓，编排单独放 infra

[ADR-0005](0005-spring-cloud-nacos.md) 已按进程拆 Gradle 工程，但仍放在同一 Git 仓里。继续同仓的代价是：一次 clone 带上全部服务，compose 路径绑死在子目录名上，而「工程独立」在版本历史上并不独立。

因此：每个 Spring 进程一个 Git 仓（`minimart-gateway`、`minimart-member-service`、`minimart-product-service`、`minimart-order-service`、`minimart-payment-service`）。原来的仓改成 **minimart-infra**：只保留 `compose.yaml`、`.env.example`、`docker/`、领域文档与 ADR。本地并列 clone，不用 submodule。compose 的 `build` 指向兄弟目录（如 `../minimart-order-service`）。

本决策取代 ADR-0005 中「同一仓库」一句。进程划分、Nacos、BOM 仍按 0005 / [0006](0006-payment-own-process.md)。
