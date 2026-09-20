package com.minimart.api.web;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.filter.OncePerRequestFilter;

import com.minimart.api.http.CorrelationHeaders;
import com.minimart.api.http.CorrelationIdHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Accepts or generates {@link CorrelationHeaders#CORRELATION_ID}, puts it in MDC, echoes it
 * on the response, and makes it visible to downstream filters (gateway proxy / MVC).
 */
public final class CorrelationIdFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String incoming = request.getHeader(CorrelationHeaders.CORRELATION_ID);
		String correlationId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming.trim();
		CorrelationIdHolder.set(correlationId);
		response.setHeader(CorrelationHeaders.CORRELATION_ID, correlationId);
		HttpServletRequest downstream = request;
		if (incoming == null || incoming.isBlank()) {
			downstream = new CorrelationRequest(request, correlationId);
		}
		try {
			filterChain.doFilter(downstream, response);
		}
		finally {
			CorrelationIdHolder.clear();
		}
	}

	private static final class CorrelationRequest extends HttpServletRequestWrapper {

		private final String correlationId;

		private CorrelationRequest(HttpServletRequest request, String correlationId) {
			super(request);
			this.correlationId = correlationId;
		}

		@Override
		public String getHeader(String name) {
			if (CorrelationHeaders.CORRELATION_ID.equalsIgnoreCase(name)) {
				return correlationId;
			}
			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			if (CorrelationHeaders.CORRELATION_ID.equalsIgnoreCase(name)) {
				return Collections.enumeration(List.of(correlationId));
			}
			return super.getHeaders(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			Set<String> names = new LinkedHashSet<>();
			Enumeration<String> existing = super.getHeaderNames();
			while (existing.hasMoreElements()) {
				names.add(existing.nextElement());
			}
			names.add(CorrelationHeaders.CORRELATION_ID);
			return Collections.enumeration(names);
		}
	}
}
