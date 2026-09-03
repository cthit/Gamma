plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":contexts:oauth"))
    implementation(project(":contexts:users"))
    implementation(project(":contexts:organization"))
    api(project(":platform:redis"))
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.authorization.server)
    implementation(libs.spring.boot.web)
    implementation(libs.kotlinx.html)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.lettuce)

    testImplementation(kotlin("test"))
    testImplementation(project(":contexts:users"))
    testImplementation(project(":contexts:oauth"))
    testImplementation(project(":platform:database"))
    testImplementation(project(":contexts:organization"))
    testImplementation(project(":test-support"))
    testRuntimeOnly(libs.spring.boot.data.redis)
}
