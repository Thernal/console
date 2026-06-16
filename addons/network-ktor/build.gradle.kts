plugins {
    alias(libs.plugins.convention.lib.core)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.networkCore)
            implementation(projects.consoleRuntime)
            implementation(libs.ktor.client.core)
        }
    }
}
