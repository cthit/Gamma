plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":platform:core"))
    api(project(":platform:database"))
    api(project(":platform"))

    testImplementation(kotlin("test"))
    testImplementation(project(":test-support"))
}
