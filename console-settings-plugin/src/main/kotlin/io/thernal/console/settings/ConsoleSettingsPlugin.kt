package io.thernal.console.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.net.URI

@Suppress("unused")
class ConsoleSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val jetbrainsDevUrl = URI("https://maven.pkg.jetbrains.space/public/p/compose/dev")

        settings.pluginManagement.repositories.maven { repo ->
            repo.url = jetbrainsDevUrl
            repo.mavenContent { content ->
                content.includeGroupAndSubgroups("org.jetbrains.compose")
                content.includeGroupAndSubgroups("org.jetbrains.androidx")
            }
        }

        @Suppress("UnstableApiUsage")
        settings.dependencyResolutionManagement.repositories.maven { repo ->
            repo.url = jetbrainsDevUrl
            repo.mavenContent { content ->
                content.includeGroupAndSubgroups("org.jetbrains.compose")
                content.includeGroupAndSubgroups("org.jetbrains.androidx")
            }
        }
    }
}
