package io.thernal.console.network.ui.addon

import io.thernal.console.network.NetworkConfig
import io.thernal.console.settings.SettingsRegistry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries

/**
 * Registered directly against [SettingsRegistry] rather than through a `ConsoleAddon.settings()`
 * hook — same reasoning as `CrashReportAddon`/`LoggingAddon`/`StepperAddon`: keeps `console-api`
 * free of a `settings-api` dependency edge.
 */
internal fun networkSettingsSection(): SettingsSection {
    return settingsEntries(
        id = "network",
        title = "Network",
        order = 40,
        config = NetworkConfig.config,
        update = NetworkConfig::updateConfig,
    ) {
        tags(
            key = "sensitiveHeaders",
            title = "Sensitive headers",
            description = "Header names masked in captured requests and responses",
            read = { it.sensitiveHeaders },
            // Lowercased on write: SensitiveHeaders.shouldMask() lowercases the incoming header
            // name but compares it against this set verbatim, so an entry typed in mixed case
            // would silently never match.
            write = { copy(sensitiveHeaders = it.map { header -> header.lowercase() }.toSet()) },
        )
    }
}
