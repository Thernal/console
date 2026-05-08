import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "az.theternal.console.buildlogic"

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("libConvention") {
            id = "az.theternal.console.lib"
            implementationClass = "az.theternal.console.buildlogic.plugins.LibConventionPlugin"
        }
        register("libCoreConvention") {
            id = "az.theternal.console.lib.core"
            implementationClass = "az.theternal.console.buildlogic.plugins.LibCoreConventionPlugin"
        }
        register("libUiConvention") {
            id = "az.theternal.console.lib.ui"
            implementationClass = "az.theternal.console.buildlogic.plugins.LibUiConventionPlugin"
        }
    }
}

