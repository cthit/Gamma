plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":contexts:apiaccess"))
    api(project(":contexts:users"))
    api(project(":contexts:oauth"))
    api(project(":contexts:organization"))
    implementation(project(":platform:core"))
    implementation(project(":platform:database"))
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testImplementation(project(":test-support"))
}
