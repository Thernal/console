package az.theternal.console.buildlogic.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class LibCoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("az.theternal.console.lib")
    }
}
