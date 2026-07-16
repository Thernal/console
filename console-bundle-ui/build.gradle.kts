plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}

// Aggregator artifact: the full console — every console module and addon in one dependency.
// Deliberately excluded: designsystem (uikit) so its types never leak into the consumer's
// compile classpath (consumers who want Ds components add it themselves), the HTTP
// interceptors (client-specific, opt-in), and the -noop variants (release-time replacements,
// not additions).
kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.consoleBundleCore)
            api(projects.consoleApi)
            api(projects.consoleUi)
            api(projects.addons.crashReportUi)
            api(projects.addons.detailsUi)
            api(projects.addons.loggingUi)
            api(projects.addons.networkUi)
            api(projects.addons.settingsApi)
            api(projects.addons.settingsUi)
            api(projects.addons.stepperUi)
        }
    }
}
