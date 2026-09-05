plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":platform:core"))
    implementation(project(":platform:database"))
    implementation(project(":platform:redis"))
    implementation(libs.bcrypt)

    testImplementation(kotlin("test"))
    testImplementation(project(":test-support"))
}
