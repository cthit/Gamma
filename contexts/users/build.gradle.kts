plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":platform:core"))
    implementation(project(":platform"))
    implementation(project(":platform:database"))
    implementation(project(":platform:redis"))
    implementation(libs.bcrypt)
    implementation(libs.kotlinx.html)
    implementation(libs.postgres)
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation(kotlin("test"))
    testImplementation(libs.logback)
    testImplementation(project(":test-support"))
}
