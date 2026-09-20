#!/bin/sh
set -eu

NACOS_ADDR="${NACOS_ADDR:-nacos:8848}"
NACOS_NS="${NACOS_NS:-dev}"
GROUP="MINIMART"
CONFIG_DIR="${CONFIG_DIR:-/nacos-seed/config}"

wait_for_nacos() {
	i=0
	while [ "$i" -lt 60 ]; do
		if curl -fsS "http://${NACOS_ADDR}/nacos/v1/console/health/readiness" >/dev/null 2>&1; then
			return 0
		fi
		i=$((i + 1))
		sleep 2
	done
	echo "nacos not ready at ${NACOS_ADDR}" >&2
	exit 1
}

ensure_namespace() {
	# Ignore already-exists; Nacos 3 still accepts the v1 console API.
	curl -sS -X POST "http://${NACOS_ADDR}/nacos/v1/console/namespaces" \
		--data-urlencode "customNamespaceId=${NACOS_NS}" \
		--data-urlencode "namespaceName=${NACOS_NS}" \
		--data-urlencode "namespaceDesc=MiniMart ${NACOS_NS}" \
		>/dev/null || true
}

publish() {
	data_id="$1"
	file="$2"
	echo "publishing ${data_id}"
	curl -fsS -X POST "http://${NACOS_ADDR}/nacos/v1/cs/configs" \
		--data-urlencode "tenant=${NACOS_NS}" \
		--data-urlencode "namespaceId=${NACOS_NS}" \
		--data-urlencode "dataId=${data_id}" \
		--data-urlencode "group=${GROUP}" \
		--data-urlencode "type=yaml" \
		--data-urlencode "content@${file}" \
		>/dev/null
}

wait_for_nacos
ensure_namespace

publish "minimart-common.yaml" "${CONFIG_DIR}/minimart-common.yaml"
publish "gateway.yaml" "${CONFIG_DIR}/gateway.yaml"
publish "member-service.yaml" "${CONFIG_DIR}/member-service.yaml"
publish "product-service.yaml" "${CONFIG_DIR}/product-service.yaml"
publish "order-service.yaml" "${CONFIG_DIR}/order-service.yaml"
publish "payment-service.yaml" "${CONFIG_DIR}/payment-service.yaml"

echo "nacos config seeded for namespace ${NACOS_NS} group ${GROUP}"
