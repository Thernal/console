package io.thernal.console.settings

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor
import org.gradle.api.initialization.Settings
import java.net.URI

@Suppress("UnstableApiUsage", "unused")
class ConsoleSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        addJetbrainsDevRepo(settings.pluginManagement.repositories)
        addJetbrainsDevRepo(settings.dependencyResolutionManagement.repositories)
    }

    private fun addJetbrainsDevRepo(handler: RepositoryHandler) {
        handler.maven(object : Action<MavenArtifactRepository> {
            override fun execute(repo: MavenArtifactRepository) {
                repo.url = URI("https://maven.pkg.jetbrains.space/public/p/compose/dev")
                repo.mavenContent(object : Action<MavenRepositoryContentDescriptor> {
                    override fun execute(content: MavenRepositoryContentDescriptor) {
                        content.includeGroupAndSubgroups("org.jetbrains.compose")
                        content.includeGroupAndSubgroups("org.jetbrains.androidx")
                    }
                })
            }
        })
    }
}
