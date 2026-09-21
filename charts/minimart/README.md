# MiniMart umbrella Helm chart (P2)

Full Deployment / Service / Ingress templates are **P2**. This directory holds the chart entry point and ConfigMap stubs.

## P2 scope

- Five Deployments + Services (gateway, member, product, order, payment)
- Ingress → gateway only
- ConfigMap `minimart-common` + per-app overlays from `../../k8s/config/`
- Secret templates for datasource passwords and JWT

## Install (future)

```bash
helm install minimart ./charts/minimart -f charts/minimart/values-dev.yaml
```

Config samples: [`k8s/config/`](../../k8s/config/).
