package com.minimart.api.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import com.minimart.api.web.CorrelationIdFilter;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.web.filter.OncePerRequestFilter")
@ConditionalOnWebApplication
public class MinimartWebAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(CorrelationIdFilter.class)
	CorrelationIdFilter correlationIdFilter() {
		return new CorrelationIdFilter();
	}
}
