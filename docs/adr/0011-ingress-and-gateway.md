---
status: accepted
---

# Ingress 与 Spring Cloud Gateway 双层入口

南北向流量采用 **两层入口**，职责不重叠：

## 集群边：Ingress / Gateway API

- TLS 终止、公网域名（如 `api.shop.example`）
- 可选：WAF、全局 IP 限流
- **不做** StripPrefix、JWT 业务校验、拦截 `/internal/**`（除非以后上 service mesh 并另写 ADR）

## 应用边：Spring Cloud Gateway（保留）

- 部署在集群内 `gateway` Deployment / Service（端口 **8080**）
- 路由：`/member/**` → `http://member-service:8081` 等（**固定 K8s Service URL**，可用 `minimart.services.*` 属性注入）
- **不**暴露 `/internal/**` 到公网前缀
- JWT 校验（尚未实现发牌）、CORS、`X-Correlation-Id`、应用级限流
- Gateway **无** MySQL

## 服务间：OpenFeign（不经 Gateway）

- order-service / payment-service 调用其他进程时使用 infra 发布的 Feign 接口
- 目标 URL 为 K8s Service DNS（如 `http://product-service:8082`），由 ConfigMap / `application.yaml` 注入
- **禁止** 经 gateway 访问 `/internal/v1/**`

## 配置载体（替换 Nacos）

| 内容 | K8s 载体 |
|------|----------|
| Jackson、Feign 超时、日志 pattern | ConfigMap `minimart-common`（样例见 `k8s/config/`） |
| datasource URL、端口 | ConfigMap + Secret |
| JWT 密钥 | Secret |
| 按环境 dev/staging/prod | Helm `charts/minimart/values.yaml` 与 `values-dev.yaml` |

Ingress 只指向 `gateway` Service；四个业务 Service 不对公网 Ingress 暴露。
