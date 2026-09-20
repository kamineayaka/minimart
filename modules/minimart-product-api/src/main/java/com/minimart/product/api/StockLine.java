package com.minimart.product.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockLine(
		@NotNull Long skuId,
		@Min(1) int qty) {
}
