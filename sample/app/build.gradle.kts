plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodel.nav3)
            implementation(projects.consoleRuntime)
            implementation(projects.consoleUi)
            implementation(projects.addons.loggingUi)
            implementation(projects.addons.detailsUi)
            implementation(projects.addons.stepperUi)
            implementation(projects.addons.networkKtor)
            implementation(projects.addons.networkUi)
            implementation(projects.addons.crashReportUi)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
        }
    }
}
