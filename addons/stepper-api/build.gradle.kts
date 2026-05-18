plugins {
    alias(libs.plugins.convention.lib.core)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.consoleRuntime)
        }
    }
}
