package io.thernal.console.buildlogic.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.applyPlugin(alias: String) {
    val plugin = libs.findPlugin(alias)
        .orElseThrow { IllegalStateException("Missing plugin alias '$alias'") }
    pluginManager.apply(plugin.get().pluginId)
}
