allprojects {
	group = "com.minimart"
	version = "0.1.0"
}

subprojects {
	plugins.withId("maven-publish") {
		extensions.configure<PublishingExtension> {
			val githubToken = System.getenv("GITHUB_TOKEN")
			if (!githubToken.isNullOrBlank()) {
				repositories {
					maven {
						name = "GitHubPackages"
						url = uri("https://maven.pkg.github.com/kamineayaka/minimart-infra")
						credentials {
							username = System.getenv("GITHUB_ACTOR") ?: "github"
							password = githubToken
						}
					}
				}
			}
		}
	}
}
