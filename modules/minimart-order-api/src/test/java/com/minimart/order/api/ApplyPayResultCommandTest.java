package com.minimart.order.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ApplyPayResultCommandTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void requiresPaymentIdStatusAndPositiveCents() {
		assertThat(validator.validate(new ApplyPayResultCommand(null, PayResultStatus.SUCCEEDED, 100L))).isNotEmpty();
		assertThat(validator.validate(new ApplyPayResultCommand(5L, PayResultStatus.SUCCEEDED, 100L))).isEmpty();
	}
}
