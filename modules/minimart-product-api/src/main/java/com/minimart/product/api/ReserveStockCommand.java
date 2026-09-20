package com.minimart.product.api;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Place-order reserve. Oversell is not allowed; last-item races have one winner.
 */
public record ReserveStockCommand(
		@NotNull Long orderId,
		@NotEmpty List<@Valid StockLine> lines) {
}
