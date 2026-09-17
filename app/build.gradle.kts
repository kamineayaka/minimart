plugins {
	java
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencies {
	implementation(project(":modules:identity"))
	implementation(project(":modules:catalog"))
	implementation(project(":modules:cart"))
	implementation(project(":modules:order"))
	implementation(project(":modules:payment"))

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")

	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	runtimeOnly("com.mysql:mysql-connector-j")
	implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.17")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
