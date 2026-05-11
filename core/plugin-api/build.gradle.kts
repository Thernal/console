plugins {
    alias(libs.plugins.convention.lib.ui)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.runtime)
            implementation(libs.jetbrains.navigation3.ui)
        }
    }
}
