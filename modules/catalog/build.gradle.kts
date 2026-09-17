plugins {
	`java-library`
	id("io.spring.dependency-management")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.1")
    }
}

dependencies {
	implementation(project(":modules:kernel"))
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
}
