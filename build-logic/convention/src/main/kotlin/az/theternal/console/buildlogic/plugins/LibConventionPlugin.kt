package az.theternal.console.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import az.theternal.console.buildlogic.config.configureAndroidTarget
import az.theternal.console.buildlogic.extensions.applyPlugin
import az.theternal.console.buildlogic.extensions.libs

/**
 * Base convention plugin for all console lib modules.
 * Configures KMP targets (Android + iOS) and shared compiler settings.
 */
@Suppress("unused")
class LibConventionPlugin : Plugin<Project> {
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
                iosX64()
                iosArm64()
                iosSimulatorArm64()
                applyDefaultHierarchyTemplate()

                val frameworkBaseName = path.removePrefix(":").replace(':', '-')
                targets.withType(KotlinNativeTarget::class.java).configureEach {
                    binaries.withType(Framework::class.java).configureEach {
                        baseName = frameworkBaseName
                        isStatic = true
                    }
                }

                sourceSets.named("commonMain") {
                    dependencies {
                        implementation(libs.findLibrary("kotlin-stdlib").orElseThrow())
                    }
                }
            }
        }
    }
}
