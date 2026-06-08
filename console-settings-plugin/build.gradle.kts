import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties

plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.33.0"
}

dependencies {
    implementation(gradleApi())
}

val rootProps = Properties().also { props ->
    rootProject.projectDir.resolve("../gradle.properties")
        .takeIf { it.exists() }
        ?.inputStream()?.use(props::load)
}
group = rootProps.getProperty("GROUP", "io.github.thernal")
version = rootProps.getProperty("VERSION", "unspecified")

gradlePlugin {
    plugins {
        register("consoleSettings") {
            id = "io.github.thernal.console"
            implementationClass = "io.thernal.console.settings.ConsoleSettingsPlugin"
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    if (!providers.gradleProperty("signingInMemoryKey").orNull.isNullOrBlank()) {
        signAllPublications()
    }
    coordinates(group.toString(), "console-settings-plugin", version.toString())
    pom {
        name.set("console-settings-plugin")
        description.set("Console KMP — Gradle settings plugin (adds required repositories)")
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
