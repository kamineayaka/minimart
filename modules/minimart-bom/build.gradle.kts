plugins {
	`java-platform`
	`maven-publish`
}

javaPlatform {
	allowDependencies()
}

dependencies {
	// Boot first; Cloud after Boot (ADR-0008, ADR-0010 — no Spring Cloud Alibaba).
	api(platform("org.springframework.boot:spring-boot-dependencies:4.0.8"))
	api(platform("org.springframework.cloud:spring-cloud-dependencies:2025.1.0"))

	constraints {
		api("com.minimart:minimart-api-common:0.1.0")
		api("com.minimart:minimart-member-api:0.1.0")
		api("com.minimart:minimart-product-api:0.1.0")
		api("com.minimart:minimart-order-api:0.1.0")
		api("com.minimart:minimart-payment-api:0.1.0")
		api("org.wiremock:wiremock-standalone:3.13.1")
	}
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["javaPlatform"])
		}
	}
}
