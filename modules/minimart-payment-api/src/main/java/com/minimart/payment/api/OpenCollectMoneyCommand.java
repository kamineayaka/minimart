package com.minimart.payment.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * order-service opens at most one collect-money record per Order.
 */
public record OpenCollectMoneyCommand(
		@NotNull Long orderId,
		@NotNull Long userId,
		@NotNull @Positive Long amountCents) {
}
