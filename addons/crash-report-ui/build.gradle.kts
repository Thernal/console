plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.crashReportCore)
            api(projects.consoleUi)
            implementation(projects.consoleRuntime)
            implementation(projects.addons.networkCore)
            implementation(projects.designsystem.components)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
        }
    }
}
