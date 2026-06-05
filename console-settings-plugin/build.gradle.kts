plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.33.0"
}

dependencies {
    implementation(gradleApi())
}

group = "io.github.thernal"
version = "0.1.0"

gradlePlugin {
    plugins {
        register("consoleSettings") {
            id = "io.github.thernal.console"
            implementationClass = "io.thernal.console.settings.ConsoleSettingsPlugin"
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    if (!providers.gradleProperty("signingInMemoryKey").orNull.isNullOrBlank()) {
        signAllPublications()
    }
    coordinates("io.github.thernal", "console-settings-plugin", "0.1.0")
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
