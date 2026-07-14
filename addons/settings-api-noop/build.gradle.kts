plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.consoleCore)
            api(libs.compose.runtime)
            api(libs.compose.foundation)
        }
    }
}
