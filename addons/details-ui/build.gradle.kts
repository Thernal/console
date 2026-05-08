plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.details)
            implementation(projects.core.pluginApi)
            implementation(projects.uikit.components)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
        }
    }
}
