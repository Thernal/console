import dev.detekt.gradle.Detekt

plugins {
    alias(libs.plugins.android.multiplatform.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("detekt.yml"))
    source.setFrom(
        subprojects
            .map { it.projectDir.resolve("src") }
            .filter { it.exists() }
    )
    parallel = true
}

dependencies {
    detektPlugins(libs.detekt.ktlint.wrapper)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
    ignoreFailures = true
    autoCorrect = false
    mustRunAfter("detektClearTodos")
    when (name) {
        "detektChanged" -> mustRunAfter("detektChangedFormat")
        "detekt"        -> mustRunAfter("detektFormat")
    }
    reports {
        checkstyle {
            required.set(true)
            outputLocation.set(
                if (name == "detektChanged")
                    layout.projectDirectory.file(".misc/detekt/detekt-report-delta.xml")
                else
                    layout.projectDirectory.file(".misc/detekt/detekt-report.xml"),
            )
        }
        sarif.required.set(false)
        html.required.set(false)
        markdown.required.set(false)
    }
}

tasks.register<Detekt>("detektChangedFormat") {
    description = "Runs ktlint autoCorrect on staged/changed .kt files only. Must run before detektChanged."
    group = "formatting"
    buildUponDefaultConfig = true
    config.setFrom(files("detekt.yml"))
    autoCorrect = true
    parallel = true
    mustRunAfter("detektClearTodos")
    reports {
        checkstyle.required.set(false)
        sarif.required.set(false)
        html.required.set(false)
        markdown.required.set(false)
    }
    val changedProp = project.findProperty("detektChangedFiles")?.toString()
    if (!changedProp.isNullOrBlank()) {
        setSource(
            changedProp.split(",").map { it.trim() }.filter { it.isNotEmpty() && it.endsWith(".kt") },
        )
    } else {
        enabled = false
    }
}

tasks.register<Detekt>("detektFormat") {
    description = "Runs ktlint autoCorrect on all source files. Must run before detekt."
    group = "formatting"
    buildUponDefaultConfig = true
    config.setFrom(files("detekt.yml"))
    autoCorrect = true
    parallel = true
    mustRunAfter("detektClearTodos")
    reports {
        checkstyle.required.set(false)
        sarif.required.set(false)
        html.required.set(false)
        markdown.required.set(false)
    }
    setSource(
        subprojects
            .map { it.projectDir.resolve("src") }
            .filter { it.exists() },
    )
}

tasks.register<Detekt>("detektChanged") {
    description = "Runs detekt on staged/changed .kt files only. Requires -PdetektChangedFiles."
    group = "verification"
    buildUponDefaultConfig = true
    config.setFrom(files("detekt.yml"))
    parallel = true
    val changedProp = project.findProperty("detektChangedFiles")?.toString()
    if (!changedProp.isNullOrBlank()) {
        setSource(
            changedProp.split(",").map { it.trim() }.filter { it.isNotEmpty() && it.endsWith(".kt") },
        )
    } else {
        enabled = false
    }
}

apply(from = "gradle/tasks/detekt.gradle.kts")
