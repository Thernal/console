package io.thernal.console.logging.ui.addon

import io.thernal.console.logging.ui.ConsoleLogObserver
import io.thernal.console.settings.SettingsRegistry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries

/**
 * Registered directly against [SettingsRegistry] rather than through a `ConsoleAddon.settings()`
 * hook — same reasoning as `CrashReportAddon`/`SettingsAddon`: keeps `console-api` free of a
 * `settings-api` dependency edge.
 */
internal fun loggingSettingsSection(): SettingsSection {
    return settingsEntries(
        id = "logging",
        title = "Logging",
        order = 10,
        config = ConsoleLogObserver.config,
        update = ConsoleLogObserver::updateConfig,
    ) {
        toggle(
            key = "enabled",
            title = "Capture enabled",
            description = "New logs are dropped while capture is off",
            read = { it.enabled },
            write = { copy(enabled = it) },
        )
        int(
            key = "maxLogCount",
            title = "Max log count",
            min = 1,
            max = 10_000,
            read = { it.maxLogCount },
            write = { copy(maxLogCount = it) },
        )
    }
}
