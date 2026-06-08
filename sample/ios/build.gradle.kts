import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val env = providers.gradleProperty("env").orElse("dev")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()

    targets.withType(KotlinNativeTarget::class.java).configureEach {
        binaries.framework {
            baseName = "SampleIos"
            isStatic = true
            export(projects.sample.app)
        }
    }

    sourceSets {
        iosMain {
            dependencies {
                api(projects.sample.app)
            }
        }
    }
}

if (env.get() != "dev") {
    configurations.configureEach {
        resolutionStrategy.dependencySubstitution {
            substitute(module("io.github.thernal:console-compose")).using(module("io.github.thernal:console-compose-noop:0.1.0"))
            substitute(module("io.github.thernal:console-details-core")).using(module("io.github.thernal:console-details-core-noop:0.1.0"))
        }
    }
}
