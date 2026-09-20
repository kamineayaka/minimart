plugins {
	`java-library`
	`maven-publish`
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(25))
	}
}

dependencies {
	api(platform(project(":minimart-bom")))
	api(project(":minimart-api-common"))
	api("org.springframework.cloud:spring-cloud-openfeign-core")
	api("org.springframework:spring-web")
	api("jakarta.validation:jakarta.validation-api")

	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.assertj:assertj-core")
	testImplementation("org.hibernate.validator:hibernate-validator")
	testImplementation("org.apache.tomcat.embed:tomcat-embed-el")
	testImplementation("tools.jackson.core:jackson-databind")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
		}
	}
}
