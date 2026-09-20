package com.minimart.api.feign;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.minimart.api.error.ApiError;
import com.minimart.api.error.ErrorCodes;
import com.minimart.api.http.CorrelationIdHolder;

import feign.Response;
import feign.codec.ErrorDecoder;
import tools.jackson.databind.json.JsonMapper;

public final class ApiErrorDecoder implements ErrorDecoder {

	private final JsonMapper jsonMapper;

	public ApiErrorDecoder() {
		this(JsonMapper.shared());
	}

	ApiErrorDecoder(JsonMapper jsonMapper) {
		this.jsonMapper = jsonMapper;
	}

	@Override
	public Exception decode(String methodKey, Response response) {
		ApiError body = readBody(response);
		if (body == null) {
			body = ApiError.of(ErrorCodes.UPSTREAM_ERROR, methodKey + " failed with HTTP " + response.status(),
					CorrelationIdHolder.get());
		}
		return new ApiStatusException(response.status(), body, methodKey);
	}

	private ApiError readBody(Response response) {
		if (response.body() == null) {
			return null;
		}
		try (InputStream in = response.body().asInputStream()) {
			byte[] bytes = in.readAllBytes();
			if (bytes.length == 0) {
				return null;
			}
			return jsonMapper.readValue(new String(bytes, StandardCharsets.UTF_8), ApiError.class);
		}
		catch (IOException | RuntimeException ex) {
			return null;
		}
	}
}
