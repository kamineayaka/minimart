package com.minimart.api.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.minimart.api.http.CorrelationHeaders;

class CorrelationIdFilterTest {

	private final CorrelationIdFilter filter = new CorrelationIdFilter();

	@Test
	void generatesAndEchoesCorrelationIdWhenMissing() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain chain = new MockFilterChain();
		filter.doFilter(request, response, chain);
		String generated = response.getHeader(CorrelationHeaders.CORRELATION_ID);
		assertThat(generated).isNotBlank();
		assertThat(((jakarta.servlet.http.HttpServletRequest) chain.getRequest())
				.getHeader(CorrelationHeaders.CORRELATION_ID)).isEqualTo(generated);
		assertThat(com.minimart.api.http.CorrelationIdHolder.get()).isNull();
	}

	@Test
	void reusesIncomingCorrelationId() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
		request.addHeader(CorrelationHeaders.CORRELATION_ID, "given-id");
		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, new MockFilterChain());
		assertThat(response.getHeader(CorrelationHeaders.CORRELATION_ID)).isEqualTo("given-id");
	}
}
