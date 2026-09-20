package com.minimart.api.http;

/**
 * Writes that may be retried must carry this header. Reads (GET) are already idempotent.
 */
public final class IdempotencyHeaders {

	public static final String KEY = "Idempotency-Key";

	private IdempotencyHeaders() {
	}
}
