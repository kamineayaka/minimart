{{- define "minimart.datasourceYaml" -}}
spring:
  datasource:
    url: {{ include "minimart.jdbcUrl" (dict "ctx" .ctx "database" .database) | quote }}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
{{- end }}

{{- define "minimart.appConfig.gateway" -}}
server:
  port: {{ .Values.gateway.service.port }}
minimart:
  services:
    member:
      host: {{ .Values.services.member.host }}
      port: {{ .Values.services.member.port }}
    product:
      host: {{ .Values.services.product.host }}
      port: {{ .Values.services.product.port }}
    order:
      host: {{ .Values.services.order.host }}
      port: {{ .Values.services.order.port }}
    payment:
      host: {{ .Values.services.payment.host }}
      port: {{ .Values.services.payment.port }}
spring:
  cloud:
    gateway:
      server:
        webmvc:
          routes:
            - id: member-service
              uri: http://${minimart.services.member.host}:${minimart.services.member.port}
              predicates:
                - Path=/member/**
              filters:
                - StripPrefix=1
            - id: product-service
              uri: http://${minimart.services.product.host}:${minimart.services.product.port}
              predicates:
                - Path=/product/**
              filters:
                - StripPrefix=1
            - id: order-service
              uri: http://${minimart.services.order.host}:${minimart.services.order.port}
              predicates:
                - Path=/order/**
              filters:
                - StripPrefix=1
            - id: payment-service
              uri: http://${minimart.services.payment.host}:${minimart.services.payment.port}
              predicates:
                - Path=/payment/**
              filters:
                - StripPrefix=1
{{- end }}

{{- define "minimart.appConfig.member-service" -}}
server:
  port: {{ .Values.memberService.service.port }}
{{ include "minimart.datasourceYaml" (dict "ctx" . "database" .Values.memberService.database) }}
{{- end }}

{{- define "minimart.appConfig.product-service" -}}
server:
  port: {{ .Values.productService.service.port }}
{{ include "minimart.datasourceYaml" (dict "ctx" . "database" .Values.productService.database) }}
{{- end }}

{{- define "minimart.appConfig.order-service" -}}
server:
  port: {{ .Values.orderService.service.port }}
spring:
  datasource:
    url: {{ include "minimart.jdbcUrl" (dict "ctx" . "database" .Values.orderService.database) | quote }}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  cloud:
    openfeign:
      client:
        config:
          product-service:
            url: {{ include "minimart.serviceUrl" .Values.services.product | quote }}
          member-service:
            url: {{ include "minimart.serviceUrl" .Values.services.member | quote }}
          payment-service:
            url: {{ include "minimart.serviceUrl" .Values.services.payment | quote }}
minimart:
  services:
    member:
      host: {{ .Values.services.member.host }}
      port: {{ .Values.services.member.port }}
    product:
      host: {{ .Values.services.product.host }}
      port: {{ .Values.services.product.port }}
    payment:
      host: {{ .Values.services.payment.host }}
      port: {{ .Values.services.payment.port }}
{{- end }}

{{- define "minimart.appConfig.payment-service" -}}
server:
  port: {{ .Values.paymentService.service.port }}
spring:
  datasource:
    url: {{ include "minimart.jdbcUrl" (dict "ctx" . "database" .Values.paymentService.database) | quote }}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  cloud:
    openfeign:
      client:
        config:
          order-service:
            url: {{ include "minimart.serviceUrl" .Values.services.order | quote }}
minimart:
  services:
    order:
      host: {{ .Values.services.order.host }}
      port: {{ .Values.services.order.port }}
{{- end }}
