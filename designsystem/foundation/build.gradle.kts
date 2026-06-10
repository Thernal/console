plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.compose.ui)
            implementation(libs.compose.foundation)
            api(libs.compose.ui.tooling.preview)
        }
    }
}
