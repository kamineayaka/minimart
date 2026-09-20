package com.minimart.member.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * order-service copies Address onto Order. member-service itself has no Feign clients.
 */
@FeignClient(name = MemberApi.SERVICE_ID, path = MemberApi.INTERNAL_ADDRESSES)
public interface MemberAddressClient {

	@GetMapping("/{addressId}")
	AddressSnapshot getAddress(@PathVariable("addressId") Long addressId, @RequestParam("userId") Long userId);
}
