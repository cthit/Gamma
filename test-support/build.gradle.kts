plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgres)
    api(libs.hikari)
    implementation(libs.testcontainers.core)
    implementation(libs.testcontainers.postgres)
    runtimeOnly(libs.postgres)

    testImplementation(kotlin("test"))
}
