package az.theternal.console.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class LibUiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("az.theternal.console.lib")
        target.pluginManager.apply("org.jetbrains.compose")
        target.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    }
}
