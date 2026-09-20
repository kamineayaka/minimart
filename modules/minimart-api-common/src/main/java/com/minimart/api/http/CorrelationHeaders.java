package com.minimart.api.http;

/**
 * Correlation id generated at gateway and propagated on Feign calls.
 */
public final class CorrelationHeaders {

	public static final String CORRELATION_ID = "X-Correlation-Id";

	public static final String MDC_KEY = "correlationId";

	private CorrelationHeaders() {
	}
}
