plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":platform:core"))
    implementation(project(":platform"))
    implementation(project(":platform:database"))
    implementation(project(":platform:redis"))
    implementation(libs.bcrypt)
    implementation(libs.kotlinx.html)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testImplementation(platform(libs.spring.boot.dependencies))
    testImplementation(project(":test-support"))
    testRuntimeOnly(libs.spring.boot.data.redis)
}
