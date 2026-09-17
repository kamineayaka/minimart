plugins {
	`java-library`
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencies {
	implementation(project(":modules:kernel"))
	implementation(project(":modules:order"))
}
