plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.consoleApi)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
    }
}
