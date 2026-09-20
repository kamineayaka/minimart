package com.minimart.api.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

class ApiErrorJsonTest {

	private final JsonMapper mapper = new JsonMapper();

	@Test
	void serializesCorrelationAndEmptyFields() {
		ApiError error = new ApiError(ErrorCodes.STOCK_INSUFFICIENT, "last SKU already reserved", "corr-1",
				Instant.parse("2026-09-20T12:00:00Z"), List.of());
		String json = mapper.writeValueAsString(error);
		assertThat(json).contains("\"code\":\"STOCK_INSUFFICIENT\"");
		assertThat(json).contains("\"correlationId\":\"corr-1\"");
		ApiError roundTrip = mapper.readValue(json, ApiError.class);
		assertThat(roundTrip.code()).isEqualTo(ErrorCodes.STOCK_INSUFFICIENT);
		assertThat(roundTrip.fields()).isEmpty();
	}
}
