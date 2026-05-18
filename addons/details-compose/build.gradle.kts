plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.detailsApi)
            implementation(projects.consoleApi)
            implementation(projects.designsystem.components)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
        }
    }
}
