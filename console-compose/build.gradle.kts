plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Console
            implementation(projects.designsystem.components)
            api(projects.consoleApi)
            // UI
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
            // Navigation
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            // State Management
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            // KotlinX
            implementation(libs.kotlinx.datetime)
        }
    }
}
