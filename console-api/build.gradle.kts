plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.consoleRuntime)
            api(projects.designsystem.components)
            api(libs.compose.ui)
            api(libs.compose.foundation)
            api(libs.jetbrains.navigation3.ui)
        }
    }
}
