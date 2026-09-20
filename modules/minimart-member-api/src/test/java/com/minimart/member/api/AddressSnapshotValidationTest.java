package com.minimart.member.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class AddressSnapshotValidationTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	void rejectsBlankReceiver() {
		AddressSnapshot snapshot = new AddressSnapshot(1L, 7L, " ", "13800000000", "上海", "上海", "浦东", "张江");
		assertThat(validator.validate(snapshot)).isNotEmpty();
	}

	@Test
	void acceptsCopyableAddress() {
		AddressSnapshot snapshot = new AddressSnapshot(1L, 7L, "张三", "13800000000", "上海", "上海", "浦东", "张江路 1 号");
		assertThat(validator.validate(snapshot)).isEmpty();
	}
}
