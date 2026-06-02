import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "io.thernal.console.buildlogic"

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
            id = "io.thernal.console.lib"
            implementationClass = "io.thernal.console.buildlogic.plugins.LibConventionPlugin"
        }
        register("libCoreConvention") {
            id = "io.thernal.console.lib.core"
            implementationClass = "io.thernal.console.buildlogic.plugins.LibCoreConventionPlugin"
        }
        register("libUiConvention") {
            id = "io.thernal.console.lib.ui"
            implementationClass = "io.thernal.console.buildlogic.plugins.LibUiConventionPlugin"
        }
    }
}

