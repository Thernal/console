plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.consoleRuntime)
            implementation(projects.designsystem.components)
            api(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.jetbrains.navigation3.ui)
        }
    }
}
