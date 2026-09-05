plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":platform:core"))
    api(libs.bundles.exposed)
    implementation(libs.hikari)
    runtimeOnly(libs.postgres)

    testImplementation(kotlin("test"))
    testImplementation(project(":test-support"))
}
