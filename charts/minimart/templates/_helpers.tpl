{{- define "minimart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" -}}
{{- end }}

{{- define "minimart.selectorLabels" -}}
app.kubernetes.io/name: {{ .name }}
app.kubernetes.io/instance: {{ .ctx.Release.Name }}
{{- end }}

{{- define "minimart.labels" -}}
helm.sh/chart: {{ include "minimart.chart" .ctx }}
{{ include "minimart.selectorLabels" . }}
app.kubernetes.io/part-of: minimart
app.kubernetes.io/managed-by: {{ .ctx.Release.Service }}
app.kubernetes.io/version: {{ .ctx.Chart.AppVersion | quote }}
{{- end }}

{{- define "minimart.image" -}}
{{- printf "%s/%s:%s" .ctx.Values.global.imageRegistry .image.repository .image.tag -}}
{{- end }}

{{- define "minimart.jdbcUrl" -}}
{{- printf "jdbc:mysql://%s:%v/%s?%s" .ctx.Values.mysql.host .ctx.Values.mysql.port .database .ctx.Values.mysql.jdbcParams -}}
{{- end }}

{{- define "minimart.serviceUrl" -}}
{{- printf "http://%s:%v" .host .port -}}
{{- end }}

{{- define "minimart.env" -}}
- name: SERVER_PORT
  value: {{ .spec.service.port | quote }}
- name: SPRING_CONFIG_IMPORT
  value: "optional:file:/etc/minimart/common/minimart-common.yaml,optional:file:/etc/minimart/app/application.yaml"
- name: MINIMART_SERVICES_MEMBER_HOST
  value: {{ .ctx.Values.services.member.host | quote }}
- name: MINIMART_SERVICES_MEMBER_PORT
  value: {{ .ctx.Values.services.member.port | quote }}
- name: MINIMART_SERVICES_PRODUCT_HOST
  value: {{ .ctx.Values.services.product.host | quote }}
- name: MINIMART_SERVICES_PRODUCT_PORT
  value: {{ .ctx.Values.services.product.port | quote }}
- name: MINIMART_SERVICES_ORDER_HOST
  value: {{ .ctx.Values.services.order.host | quote }}
- name: MINIMART_SERVICES_ORDER_PORT
  value: {{ .ctx.Values.services.order.port | quote }}
- name: MINIMART_SERVICES_PAYMENT_HOST
  value: {{ .ctx.Values.services.payment.host | quote }}
- name: MINIMART_SERVICES_PAYMENT_PORT
  value: {{ .ctx.Values.services.payment.port | quote }}
{{- if .spec.database }}
- name: DB_HOST
  value: {{ .ctx.Values.mysql.host | quote }}
- name: DB_PORT
  value: {{ .ctx.Values.mysql.port | quote }}
- name: DB_USERNAME
  value: {{ .ctx.Values.mysql.username | quote }}
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: minimart-db
      key: password
{{- end }}
{{- end }}

{{- define "minimart.deployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .name }}
  labels:
    {{- include "minimart.labels" (dict "ctx" .ctx "name" .name) | nindent 4 }}
spec:
  replicas: {{ .spec.replicaCount }}
  selector:
    matchLabels:
      {{- include "minimart.selectorLabels" (dict "ctx" .ctx "name" .name) | nindent 6 }}
  template:
    metadata:
      labels:
        {{- include "minimart.selectorLabels" (dict "ctx" .ctx "name" .name) | nindent 8 }}
      annotations:
        checksum/config-common: {{ .ctx.Files.Get "files/minimart-common.yaml" | sha256sum }}
        checksum/config-app: {{ include (printf "minimart.appConfig.%s" .name) .ctx | sha256sum }}
    spec:
      automountServiceAccountToken: false
      {{- with .ctx.Values.global.imagePullSecrets }}
      imagePullSecrets:
        {{- toYaml . | nindent 8 }}
      {{- end }}
      securityContext:
        runAsNonRoot: true
        runAsUser: 10001
        runAsGroup: 10001
        seccompProfile:
          type: RuntimeDefault
      containers:
        - name: {{ .name }}
          image: {{ include "minimart.image" (dict "ctx" .ctx "image" .spec.image) | quote }}
          imagePullPolicy: {{ .ctx.Values.global.imagePullPolicy }}
          securityContext:
            allowPrivilegeEscalation: false
            runAsNonRoot: true
            runAsUser: 10001
            runAsGroup: 10001
            capabilities:
              drop:
                - ALL
          ports:
            - name: http
              containerPort: {{ .spec.service.port }}
              protocol: TCP
          env:
            {{- include "minimart.env" . | nindent 12 }}
          volumeMounts:
            - name: common-config
              mountPath: /etc/minimart/common
              readOnly: true
            - name: app-config
              mountPath: /etc/minimart/app
              readOnly: true
          startupProbe:
            httpGet:
              path: {{ .ctx.Values.probes.path }}
              port: http
            periodSeconds: {{ .ctx.Values.probes.startup.periodSeconds }}
            timeoutSeconds: {{ .ctx.Values.probes.startup.timeoutSeconds }}
            failureThreshold: {{ .ctx.Values.probes.startup.failureThreshold }}
          readinessProbe:
            httpGet:
              path: {{ .ctx.Values.probes.path }}
              port: http
            periodSeconds: {{ .ctx.Values.probes.readiness.periodSeconds }}
            timeoutSeconds: {{ .ctx.Values.probes.readiness.timeoutSeconds }}
            failureThreshold: {{ .ctx.Values.probes.readiness.failureThreshold }}
          livenessProbe:
            httpGet:
              path: {{ .ctx.Values.probes.path }}
              port: http
            periodSeconds: {{ .ctx.Values.probes.liveness.periodSeconds }}
            timeoutSeconds: {{ .ctx.Values.probes.liveness.timeoutSeconds }}
            failureThreshold: {{ .ctx.Values.probes.liveness.failureThreshold }}
          resources:
            {{- toYaml .ctx.Values.resources | nindent 12 }}
      volumes:
        - name: common-config
          configMap:
            name: minimart-common
        - name: app-config
          configMap:
            name: {{ .name }}-config
{{- end }}

{{- define "minimart.service" -}}
apiVersion: v1
kind: Service
metadata:
  name: {{ .name }}
  labels:
    {{- include "minimart.labels" (dict "ctx" .ctx "name" .name) | nindent 4 }}
spec:
  type: ClusterIP
  selector:
    {{- include "minimart.selectorLabels" (dict "ctx" .ctx "name" .name) | nindent 4 }}
  ports:
    - name: http
      port: {{ .spec.service.port }}
      targetPort: http
      protocol: TCP
{{- end }}
