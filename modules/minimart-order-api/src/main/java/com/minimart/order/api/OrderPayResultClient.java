package com.minimart.order.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.minimart.api.http.IdempotencyHeaders;

import jakarta.validation.Valid;

/**
 * payment-service notifies order after persisting Payment SUCCEEDED/FAILED.
 * order-service owns PENDING_PAY → PAID; payment-service must not write Order.
 */
@FeignClient(name = OrderApi.SERVICE_ID, path = OrderApi.INTERNAL_ORDERS)
public interface OrderPayResultClient {

	@PostMapping("/{orderId}/pay-result")
	void applyPayResult(
			@PathVariable("orderId") Long orderId,
			@RequestHeader(IdempotencyHeaders.KEY) String idempotencyKey,
			@Valid @RequestBody ApplyPayResultCommand command);
}
