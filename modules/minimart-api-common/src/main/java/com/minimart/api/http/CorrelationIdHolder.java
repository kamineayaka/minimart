package com.minimart.api.http;

import org.slf4j.MDC;

/**
 * Process-wide holder so Feign can copy the id even when SLF4J has no provider (MDC would be NOP).
 */
public final class CorrelationIdHolder {

	private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

	private CorrelationIdHolder() {
	}

	public static void set(String correlationId) {
		CURRENT.set(correlationId);
		if (correlationId == null) {
			MDC.remove(CorrelationHeaders.MDC_KEY);
		}
		else {
			MDC.put(CorrelationHeaders.MDC_KEY, correlationId);
		}
	}

	public static String get() {
		String value = CURRENT.get();
		if (value != null && !value.isBlank()) {
			return value;
		}
		return MDC.get(CorrelationHeaders.MDC_KEY);
	}

	public static void clear() {
		CURRENT.remove();
		MDC.remove(CorrelationHeaders.MDC_KEY);
	}
}
