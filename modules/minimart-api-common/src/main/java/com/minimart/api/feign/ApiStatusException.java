package com.minimart.api.feign;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import com.minimart.api.error.ApiError;

import feign.FeignException;
import feign.Request;

public final class ApiStatusException extends FeignException {

	private final ApiError error;

	public ApiStatusException(int status, ApiError error, String methodKey) {
		super(status, message(status, error, methodKey), dummyRequest(status), error == null ? null : error.toString().getBytes(StandardCharsets.UTF_8),
				Map.of());
		this.error = error;
	}

	public ApiError error() {
		return error;
	}

	private static String message(int status, ApiError error, String methodKey) {
		String code = error == null || error.code() == null ? "UPSTREAM_ERROR" : error.code();
		String detail = error == null || error.message() == null ? "" : error.message();
		return status + " " + code + " on " + methodKey + (detail.isBlank() ? "" : ": " + detail);
	}

	private static Request dummyRequest(int status) {
		return Request.create(Request.HttpMethod.GET, "/upstream", Map.<String, Collection<String>>of(), new byte[0],
				StandardCharsets.UTF_8, null);
	}
}
