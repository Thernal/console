plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.console.compose)
            implementation(libs.console.details.compose)
            implementation(libs.console.stepper.compose)
            implementation(libs.console.network.ktor)
            implementation(libs.console.network.compose)
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
