# kind (optional, P3)

P2 checks are `helm template` and `helm lint`. This directory is a helper for a later local cluster. **Do not treat a running cluster as required.** If Docker or kind is not available, stop here.

`dev-up.sh` creates a one-node cluster named `minimart` (config: `kind-config.yaml`) and runs:

```bash
helm upgrade --install minimart charts/minimart \
  --namespace minimart --create-namespace \
  -f charts/minimart/values-dev.yaml
```

It does **not** deploy MySQL. On Linux it adds `host.docker.internal` to the kind node's `/etc/hosts` so pods can use `values-dev.yaml`'s external DB host. Match `mysql.password` to that external user (`--set mysql.password=...`). `docker/mysql/init.sql` is still applied by hand on the host.

Ingress in `values-dev.yaml` expects class `nginx` and host `api.minimart.local`, with TLS off. The chart does not install ingress-nginx. After the controller is up, point that host at 127.0.0.1.

Images are `ghcr.io/kamineayaka/minimart-*:main`. Pull them, or build and `kind load docker-image` (keep `imagePullPolicy: IfNotPresent`).

```bash
k8s/kind/dev-up.sh
# extra helm flags are forwarded, for example:
k8s/kind/dev-up.sh --set mysql.password=minimart
```
