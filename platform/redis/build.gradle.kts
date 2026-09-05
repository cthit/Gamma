plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":platform:core"))
    compileOnly(platform(libs.spring.boot.dependencies))
    compileOnly("org.springframework.data:spring-data-redis")
    implementation(libs.lettuce)

    testImplementation(kotlin("test"))
    testImplementation(platform(libs.spring.boot.dependencies))
    testImplementation(libs.spring.boot.data.redis)
    testImplementation(project(":test-support"))
}
