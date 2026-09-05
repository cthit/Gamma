pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "gamma"

include(
    ":app",
    ":api",
    ":platform",
    ":platform:database",
    ":platform:core",
    ":platform:redis",
    ":contexts:users",
    ":contexts:organization",
    ":contexts:oauth",
    ":contexts:oauth-authorization-server",
    ":contexts:apiaccess",
    ":test-support",
)
