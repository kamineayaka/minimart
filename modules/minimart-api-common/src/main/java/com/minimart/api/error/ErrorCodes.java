package com.minimart.api.error;

/**
 * Stable codes for collaboration APIs. Domain use-case handlers map onto these later.
 */
public final class ErrorCodes {

	public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

	public static final String STOCK_INSUFFICIENT = "STOCK_INSUFFICIENT";

	public static final String ADDRESS_NOT_FOUND = "ADDRESS_NOT_FOUND";

	public static final String ORDER_ILLEGAL_STATE = "ORDER_ILLEGAL_STATE";

	public static final String PAYMENT_DUPLICATE = "PAYMENT_DUPLICATE";

	public static final String IDEMPOTENCY_CONFLICT = "IDEMPOTENCY_CONFLICT";

	public static final String UPSTREAM_ERROR = "UPSTREAM_ERROR";

	private ErrorCodes() {
	}
}
