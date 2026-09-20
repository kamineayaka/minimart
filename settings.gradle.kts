pluginManagement {
	repositories {
		maven("https://maven.aliyun.com/repository/gradle-plugin")
		maven("https://maven.aliyun.com/repository/public")
		gradlePluginPortal()
		mavenCentral()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		maven("https://maven.aliyun.com/repository/public")
		maven("https://maven.aliyun.com/repository/spring")
		mavenCentral()
	}
}

rootProject.name = "minimart-infra"

include("minimart-bom")
include("minimart-api-common")
include("minimart-member-api")
include("minimart-product-api")
include("minimart-order-api")
include("minimart-payment-api")

project(":minimart-bom").projectDir = file("modules/minimart-bom")
project(":minimart-api-common").projectDir = file("modules/minimart-api-common")
project(":minimart-member-api").projectDir = file("modules/minimart-member-api")
project(":minimart-product-api").projectDir = file("modules/minimart-product-api")
project(":minimart-order-api").projectDir = file("modules/minimart-order-api")
project(":minimart-payment-api").projectDir = file("modules/minimart-payment-api")
