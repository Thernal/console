plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.networkCore)
            implementation(projects.consoleApi)
            implementation(projects.consoleCompose)
            implementation(projects.designsystem.components)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
        }
    }
}
