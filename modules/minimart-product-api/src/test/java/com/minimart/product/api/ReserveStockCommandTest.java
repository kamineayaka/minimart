package com.minimart.product.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import tools.jackson.databind.json.JsonMapper;

class ReserveStockCommandTest {

	private Validator validator;

	private final JsonMapper mapper = new JsonMapper();

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void rejectsEmptyLinesAndZeroQty() {
		assertThat(validator.validate(new ReserveStockCommand(1L, List.of()))).isNotEmpty();
		assertThat(validator.validate(new ReserveStockCommand(1L, List.of(new StockLine(9L, 0))))).isNotEmpty();
	}

	@Test
	void reservationJsonKeepsIntegerCents() {
		StockReservationView view = new StockReservationView(11L, List.of(new ReservedStockLine(9L, 2, 1999L)));
		String json = mapper.writeValueAsString(view);
		assertThat(json).contains("\"unitPriceCents\":1999");
		assertThat(json).doesNotContain("19.99");
		StockReservationView roundTrip = mapper.readValue(json, StockReservationView.class);
		assertThat(roundTrip.lines().get(0).unitPriceCents()).isEqualTo(1999L);
	}
}
