plugins {
    alias(libs.plugins.convention.lib.core)
    alias(libs.plugins.convention.publish)
}

// Aggregator artifact: everything a plain JVM/domain module can depend on (no Compose).
// The HTTP interceptors (network-ktor / network-okhttp) stay opt-in — consumers add the one
// matching their client themselves, same as the design system.
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.consoleCore)
            api(projects.consoleIo)
            api(projects.consoleRuntime)
            api(projects.addons.crashReportCore)
            api(projects.addons.detailsCore)
            api(projects.addons.networkCore)
        }
    }
}
