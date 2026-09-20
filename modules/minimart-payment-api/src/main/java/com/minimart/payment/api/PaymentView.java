package com.minimart.payment.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentView(
		@NotNull Long paymentId,
		@NotNull Long orderId,
		@NotNull @Positive Long amountCents,
		@NotNull PaymentStatus status) {
}
