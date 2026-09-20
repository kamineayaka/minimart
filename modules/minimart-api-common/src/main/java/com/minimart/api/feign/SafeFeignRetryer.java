package com.minimart.api.feign;

import java.util.Collection;

import com.minimart.api.http.IdempotencyHeaders;

import feign.Request;
import feign.RetryableException;
import feign.Retryer;

/**
 * Retries only GET (idempotent reads) or writes that already carry an idempotency key.
 */
public final class SafeFeignRetryer implements Retryer {

	private final Retryer delegate;

	public SafeFeignRetryer() {
		this(new Retryer.Default(100, 1000, 3));
	}

	SafeFeignRetryer(Retryer delegate) {
		this.delegate = delegate;
	}

	@Override
	public void continueOrPropagate(RetryableException e) {
		if (e.request() != null && isSafeToRetry(e.request())) {
			delegate.continueOrPropagate(e);
			return;
		}
		throw e;
	}

	@Override
	public Retryer clone() {
		return new SafeFeignRetryer(delegate.clone());
	}

	static boolean isSafeToRetry(Request request) {
		if (request.httpMethod() == Request.HttpMethod.GET) {
			return true;
		}
		Collection<String> keys = request.headers().get(IdempotencyHeaders.KEY);
		if (keys == null) {
			return false;
		}
		for (String key : keys) {
			if (key != null && !key.isBlank()) {
				return true;
			}
		}
		return false;
	}
}
