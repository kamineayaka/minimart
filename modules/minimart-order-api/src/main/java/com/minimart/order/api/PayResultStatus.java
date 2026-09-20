package com.minimart.order.api;

/**
 * Pay-result notification from payment-service. Duplicate SUCCEEDED is a no-op on Order.
 */
public enum PayResultStatus {

	SUCCEEDED,
	FAILED
}
