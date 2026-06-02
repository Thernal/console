package io.thernal.console.buildlogic.config

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import io.thernal.console.buildlogic.extensions.libs

private const val NAMESPACE_PREFIX = "io.thernal.console"

internal fun Project.configureAndroidTarget(extension: KotlinMultiplatformExtension) {
    val namespaceSuffix = path.removePrefix(":libs:").replace(':', '.')
    val namespace = "$NAMESPACE_PREFIX.$namespaceSuffix"

    val compileSdk = libs.findVersion("android-compile-sdk").orElseThrow().requiredVersion.toInt()
    val minSdk = libs.findVersion("android-min-sdk").orElseThrow().requiredVersion.toInt()

    extension.targets.withType(KotlinMultiplatformAndroidLibraryTarget::class.java).configureEach {
        this.namespace = namespace
        this.compileSdk = compileSdk
        this.minSdk = minSdk
    }
}
