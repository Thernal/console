plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xsuppress-warning=UNUSED_PARAMETER")
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.consoleApi)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
    }
}
