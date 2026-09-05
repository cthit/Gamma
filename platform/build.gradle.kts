plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":platform:core"))
    api(libs.kotlinx.html)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
}
