package com.minimart.api.gateway;

/**
 * Browser / Apifox prefixes on the gateway. Internal Feign paths stay under {@code /internal/**}
 * and must not be exposed here.
 */
public final class PublicRoutePrefixes {

	public static final String MEMBER = "/member";

	public static final String PRODUCT = "/product";

	public static final String ORDER = "/order";

	public static final String PAYMENT = "/payment";

	public static final String INTERNAL = "/internal";

	private PublicRoutePrefixes() {
	}
}
