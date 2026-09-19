import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
	id("org.springframework.boot") version "4.1.1" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.minimart"
	version = "0.0.1-SNAPSHOT"
}

subprojects {
	pluginManager.withPlugin("java") {
		extensions.configure<JavaPluginExtension> {
			toolchain.languageVersion.set(JavaLanguageVersion.of(25))
		}
		tasks.withType<Test>().configureEach {
			useJUnitPlatform()
		}
	}

	// Boot 4.1.1 → SCA 2025.1.0.0 → Cloud 2025.1.3 last so Oakwood is not pulled back to 2025.1.0.
	pluginManager.withPlugin("io.spring.dependency-management") {
		extensions.configure<DependencyManagementExtension> {
			imports {
				mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
				mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:2025.1.0.0")
				mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.3")
			}
		}
	}
}
