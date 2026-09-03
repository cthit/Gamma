import org.gradle.api.artifacts.ProjectDependency

plugins {
    base
    jacoco
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.detekt) apply false
}

val nettyLockOverride = libs.versions.nettyLockOverride.get()
val log4jLockOverride = libs.versions.log4jLockOverride.get()
val jacksonLockOverride = libs.versions.jacksonLockOverride.get()

val databaseMigrationTestResources by
    tasks.registering(Sync::class) {
        from(layout.projectDirectory.dir("app/src/main/resources/db/migration")) {
            into("db/migration")
        }
        into(layout.buildDirectory.dir("database-test-resources"))
    }

spotless {
    lineEndings = com.diffplug.spotless.LineEnding.UNIX

    kotlin {
        target("**/*.kt")
        targetExclude(
            "**/build/**",
            "**/generated/**",
            "**/vendor/**",
            "**/vendored/**",
        )
        ktlint(libs.versions.ktlint.get()).setEditorConfigPath(rootProject.file(".editorconfig"))
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts", "**/*.gradle.kts")
        targetExclude(
            "**/build/**",
            "**/generated/**",
            "**/vendor/**",
            "**/vendored/**",
        )
        ktlint(libs.versions.ktlint.get()).setEditorConfigPath(rootProject.file(".editorconfig"))
        trimTrailingWhitespace()
        endWithNewline()
    }
}

allprojects {
    group = "it.chalmers.gamma"
    version = "2.6.0"

    repositories {
        mavenCentral()
    }

    dependencyLocking {
        lockAllConfigurations()
        // Exposed and the Kotlin/detekt toolchain load coroutine internals. Their owning versions are locked;
        // omit these derived implementation artifacts so lockfiles describe Gamma's dependency boundary.
        ignoredDependencies.add("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        ignoredDependencies.add("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
        ignoredDependencies.add("org.jetbrains.intellij.deps.kotlinx:kotlinx-coroutines-core")
        ignoredDependencies.add("org.jetbrains.intellij.deps.kotlinx:kotlinx-coroutines-core-jvm")
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            // Keep dependency resolution aligned with the reviewed lock set until upstream BOMs
            // provide at least these versions; then remove the corresponding override.
            when {
                requested.group == "io.netty" && requested.version?.startsWith("4.2.") == true -> {
                    useVersion(nettyLockOverride)
                }

                requested.group == "org.apache.logging.log4j" && requested.name == "log4j-api" -> {
                    useVersion(log4jLockOverride)
                }

                requested.group == "tools.jackson.core" && requested.name == "jackson-databind" -> {
                    useVersion(jacksonLockOverride)
                }
            }
        }
    }
}

tasks.register("resolveAndLockAll") {
    doFirst {
        allprojects
            .flatMap { project -> project.configurations.filter { it.isCanBeResolved } }
            .forEach { it.resolve() }
    }
}

val detektCheck by
    tasks.registering {
        group = "verification"
        description = "Runs static analysis for every Kotlin module."
    }

val contextBoundaryCheck by
    tasks.registering {
        group = "verification"
        description = "Rejects dependencies between the four independent business contexts."

        doLast {
            val independentContexts =
                setOf(
                    ":contexts:apiaccess",
                    ":contexts:oauth",
                    ":contexts:organization",
                    ":contexts:users",
                )
            val violations =
                independentContexts
                    .flatMap { sourcePath ->
                        project(sourcePath).configurations.flatMap { configuration ->
                            configuration.dependencies
                                .withType<ProjectDependency>()
                                .filter { dependency -> dependency.path in independentContexts }
                                .map { dependency -> "$sourcePath:${configuration.name} -> ${dependency.path}" }
                        }
                    }.distinct()
                    .sorted()

            check(violations.isEmpty()) {
                "Business contexts must not depend on one another:\n${violations.joinToString("\n")}"
            }
        }
    }

tasks.named("check") {
    dependsOn(contextBoundaryCheck)
}

subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        apply(plugin = "dev.detekt")
        apply(plugin = "jacoco")
        val moduleCheck = tasks.named("check")
        rootProject.tasks.named("check") {
            dependsOn(moduleCheck)
        }
        detektCheck.configure {
            dependsOn(tasks.withType<dev.detekt.gradle.Detekt>())
        }

        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
            compilerOptions {
                allWarningsAsErrors.set(true)
                freeCompilerArgs.add("-Xjsr305=strict")
            }
        }

        extensions.configure<dev.detekt.gradle.extensions.DetektExtension> {
            buildUponDefaultConfig.set(true)
            config.from(rootProject.files("config/detekt/detekt.yml"))
            basePath.set(rootProject.layout.projectDirectory)
        }

        tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
            exclude(
                "**/build/**",
                "**/generated/**",
                "**/vendor/**",
                "**/vendored/**",
            )
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            systemProperty("gamma.root", rootProject.projectDir.absolutePath)
            val directlyUsesDatabase =
                providers.provider {
                    project.path != ":app" &&
                        project.configurations
                            .getByName("testRuntimeClasspath")
                            .allDependencies
                            .withType<ProjectDependency>()
                            .any { dependency -> dependency.path == ":platform:database" }
                }
            val databaseResourceClasspath =
                directlyUsesDatabase.map { usesDatabase ->
                    if (usesDatabase) {
                        listOf(databaseMigrationTestResources.get().destinationDir)
                    } else {
                        emptyList()
                    }
                }

            // Database consumer tests exercise the migrations shipped by the assembled app.
            dependsOn(
                directlyUsesDatabase.map { usesDatabase ->
                    if (usesDatabase) listOf(databaseMigrationTestResources) else emptyList()
                },
            )
            classpath += rootProject.files(databaseResourceClasspath)
            testLogging {
                events("failed", "skipped")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }
        }
    }
}

val aggregateCoverageReport by tasks.registering(org.gradle.testing.jacoco.tasks.JacocoReport::class) {
    group = "verification"
    description = "Generates aggregate Kotlin coverage for every active production module."
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

val unitTest by tasks.registering {
    group = "verification"
    description = "Runs tests that do not require external service containers."
}

val integrationTest by tasks.registering {
    group = "verification"
    description = "Runs the container-backed integration tests."
}

val aggregateCoverageVerification by
    tasks.registering(org.gradle.testing.jacoco.tasks.JacocoCoverageVerification::class) {
        group = "verification"
        description = "Enforces the repository-wide behavioral coverage floor."
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.70".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.55".toBigDecimal()
                }
            }
        }
    }

subprojects {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        val testTask = tasks.named<Test>("test")
        val moduleUnitTest =
            tasks.register<Test>("unitTest") {
                testClassesDirs = testTask.get().testClassesDirs
                classpath = testTask.get().classpath
                filter {
                    excludeTestsMatching("*IntegrationTest")
                    isFailOnNoMatchingTests = false
                }
            }
        val moduleIntegrationTest =
            tasks.register<Test>("integrationTest") {
                testClassesDirs = testTask.get().testClassesDirs
                classpath = testTask.get().classpath
                filter {
                    includeTestsMatching("*IntegrationTest")
                    isFailOnNoMatchingTests = false
                }
            }
        rootProject.tasks.named("unitTest") { dependsOn(moduleUnitTest) }
        rootProject.tasks.named("integrationTest") { dependsOn(moduleIntegrationTest) }
        aggregateCoverageReport.configure {
            dependsOn(testTask)
            executionData.from(layout.buildDirectory.file("jacoco/test.exec"))
            sourceDirectories.from(layout.projectDirectory.dir("src/main/kotlin"))
            classDirectories.from(layout.buildDirectory.dir("classes/kotlin/main"))
        }
        aggregateCoverageVerification.configure {
            dependsOn(testTask)
            executionData.from(layout.buildDirectory.file("jacoco/test.exec"))
            sourceDirectories.from(layout.projectDirectory.dir("src/main/kotlin"))
            classDirectories.from(layout.buildDirectory.dir("classes/kotlin/main"))
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.named("spotlessCheck"))
    dependsOn(aggregateCoverageVerification, detektCheck)
}
