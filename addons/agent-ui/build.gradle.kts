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
            implementation(libs.ktor.network)
            implementation(libs.kotlinx.serialization.json)
        }
        // Only the Android device reader needs it, for the app's package name.
        androidMain.dependencies {
            implementation(projects.consoleIo)
        }
    }
}
