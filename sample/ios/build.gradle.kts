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
            substitute(project(":console-compose")).using(project(":console-compose-noop"))
            substitute(project(":addons:details-api")).using(project(":addons:details-core-noop"))
        }
    }
}
