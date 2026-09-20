package com.minimart.payment.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.minimart.api.http.IdempotencyHeaders;

import jakarta.validation.Valid;

/**
 * order-service opens collect-money. payment-service never writes Order or Stock.
 */
@FeignClient(name = PaymentApi.SERVICE_ID, path = PaymentApi.INTERNAL_PAYMENTS)
public interface PaymentClient {

	@PostMapping
	PaymentView openCollectMoney(
			@RequestHeader(IdempotencyHeaders.KEY) String idempotencyKey,
			@Valid @RequestBody OpenCollectMoneyCommand command);
}
