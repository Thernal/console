plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.uikit.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
        }
    }
}
