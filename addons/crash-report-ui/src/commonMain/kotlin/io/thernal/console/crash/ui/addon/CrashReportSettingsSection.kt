package io.thernal.console.crash.ui.addon

import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.config.CrashBodyPolicy
import io.thernal.console.crash.ui.config.CrashReportConfig
import io.thernal.console.settings.SettingsEntriesScope
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries

/**
 * Registered directly against `SettingsRegistry` rather than through a `ConsoleAddon.settings()`
 * hook — that hook would add a `console-api` → `settings-api` dependency edge for every addon,
 * even ones that never register settings; the settings addon itself already registers its own
 * built-in section the same way.
 */
internal fun crashReportSettingsSection(): SettingsSection {
    return settingsEntries(
        id = "crash-report",
        title = "Crash Report",
        order = 30,
        config = CrashReports.config,
        update = CrashReports::updateConfig,
    ) {
        visibilityEntries()
        persistFilterEntries()
        bodyAndRetentionEntries()
    }
}

private fun SettingsEntriesScope<CrashReportConfig>.visibilityEntries() {
    toggle(
        key = "showSafeSessions",
        title = "Show safe sessions",
        description = "List background and clean terminations too",
        read = { it.showSafeSessions },
        write = { copy(showSafeSessions = it) },
    )
}

private fun SettingsEntriesScope<CrashReportConfig>.persistFilterEntries() {
    toggle(
        key = "persistOnMatch",
        title = "Persist on match",
        description = "Only logs matching the criteria below are written to a session",
        read = { it.persistOnMatch },
        write = { copy(persistOnMatch = it) },
    )
    enum(
        key = "persistLevelAtLeast",
        title = "Persist level at least",
        options = LogLevel.entries.filter { it != LogLevel.None },
        noneLabel = "Any",
        enabledWhen = { it.persistOnMatch },
        read = { it.persistLevelAtLeast },
        write = { copy(persistLevelAtLeast = it) },
    )
    tags(
        key = "includeTags",
        title = "Include tags",
        enabledWhen = { it.persistOnMatch },
        read = { it.includeTags },
        write = { copy(includeTags = it) },
    )
    tags(
        key = "excludeTags",
        title = "Exclude tags",
        enabledWhen = { it.persistOnMatch },
        read = { it.excludeTags },
        write = { copy(excludeTags = it) },
    )
}

private fun SettingsEntriesScope<CrashReportConfig>.bodyAndRetentionEntries() {
    enum(
        key = "bodyPolicy",
        title = "Body policy",
        options = CrashBodyPolicy.entries,
        read = { it.bodyPolicy },
        write = { copy(bodyPolicy = it!!) },
    )
    int(
        key = "maxBodySize",
        title = "Max body size",
        min = 256,
        max = 65_536,
        read = { it.maxBodySize },
        write = { copy(maxBodySize = it) },
    )
    int(
        key = "maxSessions",
        title = "Max sessions",
        min = 1,
        max = 100,
        read = { it.maxSessions },
        write = { copy(maxSessions = it) },
    )
}
