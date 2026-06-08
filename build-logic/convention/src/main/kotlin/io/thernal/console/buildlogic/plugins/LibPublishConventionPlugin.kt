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
            val artifactId = if (project.name.startsWith("console-")) {
                project.name
            } else {
                "console-${project.name}"
            }

            // Must be set BEFORE apply — vanniktech reads and finalizes these on apply
            project.group = group
            project.version = consoleVersion
            project.extensions.extraProperties["POM_ARTIFACT_ID"] = artifactId

            pluginManager.apply("com.vanniktech.maven.publish")

            extensions.configure<MavenPublishBaseExtension> {
                publishToMavenCentral(automaticRelease = true)
                if (!providers.gradleProperty("signingInMemoryKey").orNull.isNullOrBlank()) {
                    signAllPublications()
                }
                pom {
                    name.set(artifactId)
                    description.set("Console KMP — $artifactId")
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
