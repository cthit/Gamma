plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spring.boot)
}

application {
    mainClass.set("it.chalmers.gamma.GammaApplicationKt")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":contexts:users"))
    implementation(project(":contexts:organization"))
    implementation(project(":contexts:oauth"))
    implementation(project(":contexts:oauth-authorization-server"))
    implementation(project(":contexts:apiaccess"))
    implementation(project(":platform"))
    implementation(project(":platform:core"))
    implementation(project(":platform:database"))
    implementation(project(":platform:redis"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.web)
    implementation(libs.spring.boot.security)
    implementation(libs.spring.boot.authorization.server)
    implementation(libs.spring.boot.data.redis)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.session.data.redis)

    runtimeOnly(libs.postgres)
    runtimeOnly(libs.flyway.postgres)
    runtimeOnly("org.springframework.boot:spring-boot-starter-flyway")
    runtimeOnly("org.webjars.npm:htmx.org:1.9.12")
    runtimeOnly("org.webjars.npm:hyperscript.org:0.9.12")
    runtimeOnly("org.webjars.npm:picocss__pico:2.0.6")
    runtimeOnly("org.webjars.npm:sortablejs:1.15.3")

    testImplementation(kotlin("test"))
    testImplementation(libs.spring.boot.test) {
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }
    testImplementation(project(":test-support"))
}

tasks.bootBuildImage {
    imageName.set("app:latest")
    environment.set(mapOf("BP_JVM_VERSION" to "25.*"))
}
