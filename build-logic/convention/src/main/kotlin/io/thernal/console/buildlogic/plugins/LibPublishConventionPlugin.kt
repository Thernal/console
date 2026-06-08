package io.thernal.console.buildlogic.plugins

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("unused")
class LibPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val group = providers.gradleProperty("GROUP").get()
            val consoleVersion = providers.gradleProperty("VERSION").get()

            // Must be set BEFORE apply — vanniktech reads and finalizes these on apply
            project.group = group
            project.version = consoleVersion

            pluginManager.apply("com.vanniktech.maven.publish")

            extensions.configure<MavenPublishBaseExtension> {
                publishToMavenCentral(automaticRelease = true)
                if (!providers.gradleProperty("signingInMemoryKey").orNull.isNullOrBlank()) {
                    signAllPublications()
                }
                pom {
                    name.set(project.name)
                    description.set("Console KMP — ${project.name}")
                    url.set("https://github.com/Thernal/Console")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("hhumbetovv")
                            name.set("Humbat Humbatov")
                        }
                    }
                    scm {
                        url.set("https://github.com/Thernal/Console")
                        connection.set("scm:git:git://github.com/Thernal/Console.git")
                        developerConnection.set("scm:git:ssh://git@github.com/Thernal/Console.git")
                    }
                }
            }
        }
    }
}
