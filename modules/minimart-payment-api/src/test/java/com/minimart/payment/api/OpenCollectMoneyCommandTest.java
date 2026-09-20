package com.minimart.payment.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class OpenCollectMoneyCommandTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void amountMustBePositiveCents() {
		assertThat(validator.validate(new OpenCollectMoneyCommand(1L, 7L, 0L))).isNotEmpty();
		assertThat(validator.validate(new OpenCollectMoneyCommand(1L, 7L, 3900L))).isEmpty();
	}
}
