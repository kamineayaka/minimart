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
	api("jakarta.validation:jakarta.validation-api")
	implementation("org.slf4j:slf4j-api")
	implementation("tools.jackson.core:jackson-databind")

	compileOnly("org.springframework.boot:spring-boot-autoconfigure")
	compileOnly("org.springframework:spring-context")
	compileOnly("org.springframework:spring-web")
	compileOnly("jakarta.servlet:jakarta.servlet-api")
	compileOnly("io.github.openfeign:feign-core")

	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.assertj:assertj-core")
	testImplementation("org.springframework:spring-web")
	testImplementation("org.springframework:spring-context")
	testImplementation("org.springframework:spring-test")
	testImplementation("jakarta.servlet:jakarta.servlet-api")
	testImplementation("io.github.openfeign:feign-core")
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
