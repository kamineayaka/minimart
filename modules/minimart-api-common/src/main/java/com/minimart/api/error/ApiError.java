package com.minimart.api.error;

import java.time.Instant;
import java.util.List;

/**
 * Shared error body for internal Feign APIs and (later) public JSON errors.
 */
public record ApiError(
		String code,
		String message,
		String correlationId,
		Instant timestamp,
		List<ApiFieldError> fields) {

	public ApiError {
		fields = fields == null ? List.of() : List.copyOf(fields);
	}

	public static ApiError of(String code, String message, String correlationId) {
		return new ApiError(code, message, correlationId, Instant.now(), List.of());
	}
}
