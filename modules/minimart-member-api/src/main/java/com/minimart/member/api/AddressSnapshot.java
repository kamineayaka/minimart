package com.minimart.member.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Address content copied onto an Order at place-order time. Later User edits must not change this snapshot.
 */
public record AddressSnapshot(
		@NotNull Long addressId,
		@NotNull Long userId,
		@NotBlank String receiverName,
		@NotBlank String phone,
		@NotBlank String province,
		@NotBlank String city,
		String district,
		@NotBlank String detail) {
}
