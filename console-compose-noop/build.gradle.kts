plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
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
