plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.crashReportCore)
            api(projects.consoleApi)
            implementation(projects.consoleRuntime)
            implementation(projects.addons.networkCore)
        }
    }
}
