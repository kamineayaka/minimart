package com.minimart.order.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ApplyPayResultCommand(
		@NotNull Long paymentId,
		@NotNull PayResultStatus status,
		@NotNull @Positive Long amountCents) {
}
