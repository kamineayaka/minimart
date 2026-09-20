package com.minimart.api.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.minimart.api.feign.ApiErrorDecoder;
import com.minimart.api.feign.CorrelationIdRequestInterceptor;
import com.minimart.api.feign.SafeFeignRetryer;

import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;

/**
 * Imported only by processes that use OpenFeign (order-service, payment-service).
 * Not an {@code AutoConfiguration} — loading this class requires Feign on the classpath.
 */
@Configuration(proxyBeanMethods = false)
public class MinimartFeignConfiguration {

	@Bean
	@ConditionalOnMissingBean(CorrelationIdRequestInterceptor.class)
	RequestInterceptor minimartCorrelationIdRequestInterceptor() {
		return new CorrelationIdRequestInterceptor();
	}

	@Bean
	@ConditionalOnMissingBean(Retryer.class)
	Retryer minimartFeignRetryer() {
		return new SafeFeignRetryer();
	}

	@Bean
	@ConditionalOnMissingBean(ErrorDecoder.class)
	ErrorDecoder minimartApiErrorDecoder() {
		return new ApiErrorDecoder();
	}
}
