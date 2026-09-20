package com.minimart.product.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.minimart.api.http.IdempotencyHeaders;

import jakarta.validation.Valid;

/**
 * order-service collaborates on Stock. product-service itself has no Feign clients.
 * Confirm happens after Payment SUCCEEDED; release on cancel/timeout. product-service
 * owns the Stock table — callers must not write it.
 */
@FeignClient(name = ProductApi.SERVICE_ID, path = ProductApi.INTERNAL_STOCK)
public interface ProductStockClient {

	@PostMapping("/reservations")
	StockReservationView reserve(
			@RequestHeader(IdempotencyHeaders.KEY) String idempotencyKey,
			@Valid @RequestBody ReserveStockCommand command);

	@PostMapping("/reservations/{orderId}/confirmation")
	void confirm(
			@PathVariable("orderId") Long orderId,
			@RequestHeader(IdempotencyHeaders.KEY) String idempotencyKey,
			@Valid @RequestBody ConfirmStockCommand command);

	@PostMapping("/reservations/{orderId}/release")
	void release(
			@PathVariable("orderId") Long orderId,
			@RequestHeader(IdempotencyHeaders.KEY) String idempotencyKey,
			@Valid @RequestBody ReleaseStockCommand command);
}
