plugins {
    alias(libs.plugins.convention.lib.core)
    alias(libs.plugins.convention.publish)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xsuppress-warning=UNUSED_PARAMETER")
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
        }
    }
}
