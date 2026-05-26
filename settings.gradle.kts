@file:Suppress("UnstableApiUsage")

rootProject.name = "Console"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
            mavenContent {
                includeGroupAndSubgroups("org.jetbrains.compose")
                includeGroupAndSubgroups("org.jetbrains.androidx")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
            mavenContent {
                includeGroupAndSubgroups("org.jetbrains.compose")
                includeGroupAndSubgroups("org.jetbrains.androidx")
            }
        }
    }
}

includeBuild("build-logic")

// ── Auto-configure git hooks ──────────────────────────────────────────────────
// Runs on every Gradle sync. Sets core.hooksPath → .githooks in .git/config
// only when it is not already pointing to .githooks, so the pre-commit detekt
// hook is active for everyone without any manual setup step.
run {
    val gitConfig = settingsDir.resolve(".git/config")
    if (!gitConfig.exists()) return@run

    val content = gitConfig.readText()

    if (Regex("""hooksPath\s*=\s*\.githooks""").containsMatchIn(content)) return@run

    if ("hooksPath" in content) {
        gitConfig.writeText(
            content.replace(Regex("""(hooksPath\s*=\s*)[^\r\n]*"""), "$1.githooks"),
        )
    } else {
        gitConfig.appendText("\n[core]\n\thooksPath = .githooks\n")
    }

    println("  ✓ Git hooks path configured → .githooks")
}

include(":sample:app")
include(":sample:android")
include(":sample:ios")
include(":sample:jvm")

include(":console-runtime")
include(":console-api")
include(":console-compose")
include(":console-compose-noop")
include(":designsystem:foundation")
include(":designsystem:components")

// Auto-discover addon modules
settingsDir.resolve("addons").walkTopDown()
    .filter { it.resolve("build.gradle.kts").exists() }
    .forEach { dir ->
        val path = ":addons:" + dir.relativeTo(settingsDir.resolve("addons"))
            .path.replace(File.separator, ":")
        include(path)
    }
