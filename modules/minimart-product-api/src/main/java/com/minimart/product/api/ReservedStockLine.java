package com.minimart.product.api;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Reserved qty plus the SKU unit price snapshot in integer cents (ADR-0004).
 */
public record ReservedStockLine(
		@NotNull Long skuId,
		@Min(1) int qty,
		@Min(0) long unitPriceCents) {
}
