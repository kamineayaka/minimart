package com.minimart.api.feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import com.minimart.api.http.CorrelationHeaders;
import com.minimart.api.http.CorrelationIdHolder;
import com.minimart.api.http.IdempotencyHeaders;

import feign.Request;
import feign.RequestTemplate;
import feign.RetryableException;
import feign.Retryer;

class FeignCollaborationSupportTest {

	@Test
	void interceptorCopiesMdcCorrelationId() {
		CorrelationIdHolder.set("from-gateway");
		try {
			RequestTemplate template = new RequestTemplate();
			new CorrelationIdRequestInterceptor().apply(template);
			assertThat(template.headers().get(CorrelationHeaders.CORRELATION_ID)).containsExactly("from-gateway");
		}
		finally {
			CorrelationIdHolder.clear();
		}
	}

	@Test
	void retriesGetAndIdempotentWritesOnly() {
		assertThat(SafeFeignRetryer.isSafeToRetry(request(Request.HttpMethod.GET, Map.of()))).isTrue();
		assertThat(SafeFeignRetryer.isSafeToRetry(request(Request.HttpMethod.POST, Map.of()))).isFalse();
		assertThat(SafeFeignRetryer.isSafeToRetry(
				request(Request.HttpMethod.POST, Map.of(IdempotencyHeaders.KEY, List.of("k-1"))))).isTrue();
	}

	@Test
	void retryerPropagatesUnsafeWrites() {
		Retryer retryer = new SafeFeignRetryer();
		RetryableException failure = new RetryableException(503, "down", Request.HttpMethod.POST, 0L,
				request(Request.HttpMethod.POST, Map.of()));
		assertThatThrownBy(() -> retryer.continueOrPropagate(failure)).isSameAs(failure);
	}

	private static Request request(Request.HttpMethod method, Map<String, Collection<String>> headers) {
		return Request.create(method, "/internal/v1/example", headers, new byte[0], StandardCharsets.UTF_8, null);
	}
}
