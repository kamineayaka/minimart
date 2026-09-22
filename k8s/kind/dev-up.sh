#!/usr/bin/env bash
# Optional P3 helper: kind cluster + helm upgrade of charts/minimart.
# P2 does not require this script. It does not deploy MySQL, Nacos, Kafka, or Redis.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CLUSTER="${KIND_CLUSTER_NAME:-minimart}"

need() {
	if ! command -v "$1" >/dev/null 2>&1; then
		echo "missing '$1' — kind install is optional; helm template does not need a cluster" >&2
		exit 1
	fi
}

need kind
need kubectl
need helm
need docker

if ! kind get clusters 2>/dev/null | grep -qx "$CLUSTER"; then
	kind create cluster --name "$CLUSTER" --config "$ROOT/k8s/kind/kind-config.yaml"
fi

# Linux Docker does not provide host.docker.internal inside the node. Pods use that name
# (values-dev mysql.host) to reach MySQL on the host. This does not start MySQL.
if ! docker exec "${CLUSTER}-control-plane" getent hosts host.docker.internal >/dev/null 2>&1; then
	gateway_ip="$(docker exec "${CLUSTER}-control-plane" ip -4 route show default | awk '{print $3}')"
	docker exec "${CLUSTER}-control-plane" sh -c "grep -q host.docker.internal /etc/hosts || echo '${gateway_ip} host.docker.internal' >> /etc/hosts"
fi

kubectl config use-context "kind-${CLUSTER}"
helm upgrade --install minimart "$ROOT/charts/minimart" \
	--namespace minimart --create-namespace \
	-f "$ROOT/charts/minimart/values-dev.yaml" \
	"$@"

cat <<EOF
Installed release minimart in namespace minimart.
Ingress host api.minimart.local stays dark until an ingress controller is installed, for example:
  kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
MySQL is external. Pods will not become Ready until images exist and the database accepts DB_PASSWORD.
EOF
