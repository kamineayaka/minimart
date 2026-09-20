package com.minimart.api.feign;

import com.minimart.api.http.CorrelationHeaders;
import com.minimart.api.http.CorrelationIdHolder;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public final class CorrelationIdRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		if (hasHeader(template, CorrelationHeaders.CORRELATION_ID)) {
			return;
		}
		String correlationId = CorrelationIdHolder.get();
		if (correlationId != null && !correlationId.isBlank()) {
			template.header(CorrelationHeaders.CORRELATION_ID, correlationId);
		}
	}

	private static boolean hasHeader(RequestTemplate template, String name) {
		var values = template.headers().get(name);
		return values != null && !values.isEmpty();
	}
}
