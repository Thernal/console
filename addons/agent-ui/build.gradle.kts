plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.agentApi)
            api(projects.consoleUi)
            implementation(projects.addons.settingsApi)
            implementation(projects.consoleIo)
            implementation(libs.ktor.network)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
