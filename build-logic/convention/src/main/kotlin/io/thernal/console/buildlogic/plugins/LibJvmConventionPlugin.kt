package io.thernal.console.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import io.thernal.console.buildlogic.config.configureAndroidTarget
import io.thernal.console.buildlogic.extensions.applyPlugin
import io.thernal.console.buildlogic.extensions.libs

/**
 * Convention plugin for modules that target Android and JVM only (no iOS).
 * Use for addons that depend on Android/JVM-only libraries (e.g. OkHttp).
 */
@Suppress("unused")
class LibJvmConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val jvm = libs.findVersion("jvm").orElseThrow().requiredVersion.toInt()

            applyPlugin("kotlin.multiplatform")
            applyPlugin("android.multiplatform.library")
            applyPlugin("android.lint")

            extensions.configure<KotlinMultiplatformExtension> {
                jvmToolchain(jvm)

                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-Xexpect-actual-classes",
                    )
                }

                sourceSets.all {
                    languageSettings {
                        optIn("kotlin.time.ExperimentalTime")
                        optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                    }
                }

                configureAndroidTarget(this)
                jvm()
                applyDefaultHierarchyTemplate()

                sourceSets.named("commonMain") {
                    dependencies {
                        implementation(libs.findLibrary("kotlin-stdlib").orElseThrow())
                    }
                }
            }
        }
    }
}
