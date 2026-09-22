# MiniMart umbrella chart

Kubernetes manifests for the five OLTP processes. Ingress terminates the public host and forwards to **gateway only**. Spring Cloud Gateway stays in-cluster. Feign uses Service DNS and does not go through the gateway.

MySQL is **not** installed by this chart. Nacos, Kafka, lake, and Redis are not installed (`*.enabled` defaults are `false`; turning one on fails the render).

## Layout

| Path | What |
|------|------|
| `templates/*-deployment.yaml` / `*-service.yaml` | gateway `:8080`, member-service `:8081`, product-service `:8082`, order-service `:8083`, payment-service `:8084` |
| `templates/ingress.yaml` | host from values; TLS only when `ingress.tls` is true |
| `templates/configmap-common.yaml` | ConfigMap `minimart-common` (same bytes as [`k8s/config/minimart-common.yaml`](../../k8s/config/minimart-common.yaml)) |
| `templates/configmap-apps.yaml` | per-app `application.yaml` (ports, external JDBC URL, Feign/Gateway URLs) |
| `templates/secret-db.yaml` | Secret `minimart-db` password **placeholder** |
| `templates/secret-jwt.yaml` | Secret `minimart-jwt` placeholder (not mounted; JWT is not implemented) |
| `values.yaml` | documented defaults (`ghcr.io/kamineayaka`, tag `main`) |
| `values-dev.yaml` | kind/local: `api.minimart.local`, TLS off, `host.docker.internal` |

Pods mount both ConfigMaps and set `SPRING_CONFIG_IMPORT` so Spring Boot reads them. DB host, port, and username are env vars. `DB_PASSWORD` comes from the Secret. Images run as uid **10001** (see each service `Dockerfile`). Probes use `/actuator/health`.

## Render and lint

From the **minimart-infra** root:

```bash
helm template minimart charts/minimart -f charts/minimart/values-dev.yaml
helm lint charts/minimart -f charts/minimart/values-dev.yaml
```

## Install

```bash
helm upgrade --install minimart charts/minimart \
  --namespace minimart --create-namespace \
  -f charts/minimart/values-dev.yaml \
  --set mysql.password=placeholder
```

`--set mysql.password` must match the **external** MySQL user. `docker/mysql/init.sql` on a dev host creates `minimart` / `minimart`; pass that only on the machine that owns the database. Do not commit it.

External JDBC URLs (password omitted; the Secret supplies `DB_PASSWORD`):

```text
jdbc:mysql://<mysql.host>:<mysql.port>/minimart_member?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
jdbc:mysql://<mysql.host>:<mysql.port>/minimart_product?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
jdbc:mysql://<mysql.host>:<mysql.port>/minimart_order?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
jdbc:mysql://<mysql.host>:<mysql.port>/minimart_payment?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
```

`values.yaml` uses host `mysql.example.invalid`. `values-dev.yaml` uses `host.docker.internal`.

Images (tag `main`):

```text
ghcr.io/kamineayaka/minimart-gateway:main
ghcr.io/kamineayaka/minimart-member-service:main
ghcr.io/kamineayaka/minimart-product-service:main
ghcr.io/kamineayaka/minimart-order-service:main
ghcr.io/kamineayaka/minimart-payment-service:main
```

Feign and Gateway targets in `values-dev.yaml`:

```text
http://member-service:8081
http://product-service:8082
http://order-service:8083
http://payment-service:8084
```

## Kind

A live cluster is **P3**. Optional helper (needs Docker, kind, kubectl, helm): [`k8s/kind/README.md`](../../k8s/kind/README.md).

```bash
k8s/kind/dev-up.sh
```

If those tools are missing, skip the script. `helm template` / `helm lint` are the P2 checks.
