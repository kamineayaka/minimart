package com.minimart.payment.api;

/**
 * Payment record status. Order becomes PAID only after {@link #SUCCEEDED}.
 */
public enum PaymentStatus {

	PENDING,
	SUCCEEDED,
	FAILED
}
