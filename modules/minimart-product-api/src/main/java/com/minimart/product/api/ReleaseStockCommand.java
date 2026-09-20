package com.minimart.product.api;

import jakarta.validation.constraints.NotNull;

public record ReleaseStockCommand(@NotNull Long orderId) {
}
