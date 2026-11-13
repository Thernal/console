package io.thernal.console.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

/**
 * Guards a module's public API with Kotlin's own ABI validation. The module keeps the API it publishes in `api/`
 * (`<module>.api` for Android and JVM, `<module>.klib.api` for iOS): `checkKotlinAbi`, part of `check`, fails when
 * the code no longer matches it, and `updateKotlinAbi` writes it again after an intended change, so every change to
 * the API shows in the diff. Declarations marked `@ConsoleInternalApi` are left out: they may change in any release.
 *
 * Kotlin 2.4 dumps the JVM and iOS targets but not the Android one AGP 9's KMP library plugin makes: common code is
 * covered through the JVM dump, the few declarations only `androidMain` has are not.
 *
 * Applied by `io.thernal.console.publish`, so every published module is guarded.
 */
@Suppress("unused")
class LibAbiConventionPlugin : Plugin<Project> {
    @OptIn(ExperimentalAbiValidation::class)
    override fun apply(target: Project) {
        target.pluginManager.withPlugin(KOTLIN_MULTIPLATFORM) {
            target.extensions.configure<KotlinMultiplatformExtension> {
                abiValidation {
                    filters {
                        exclude {
                            annotatedWith.add(INTERNAL_API)
                        }
                    }
                }
            }
        }
    }

    private companion object {
        const val KOTLIN_MULTIPLATFORM = "org.jetbrains.kotlin.multiplatform"
        const val INTERNAL_API = "io.thernal.console.core.ConsoleInternalApi"
    }
}
