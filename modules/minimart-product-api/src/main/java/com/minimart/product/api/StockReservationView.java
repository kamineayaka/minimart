package com.minimart.product.api;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record StockReservationView(
		@NotNull Long orderId,
		@NotEmpty List<ReservedStockLine> lines) {
}
