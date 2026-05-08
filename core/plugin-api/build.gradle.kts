plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation3.ui)
        }
    }
}
