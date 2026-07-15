package io.thernal.console.stepper.ui.addon

import io.thernal.console.core.log.LogLevel
import io.thernal.console.settings.SettingsEntriesScope
import io.thernal.console.settings.SettingsRegistry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries
import io.thernal.console.stepper.ui.stepper.AutoResumeDelay
import io.thernal.console.stepper.ui.stepper.Stepper

/**
 * Registered directly against [SettingsRegistry] rather than through a `ConsoleAddon.settings()`
 * hook — same reasoning as `CrashReportAddon`/`LoggingAddon`/`SettingsAddon`: keeps `console-api`
 * free of a `settings-api` dependency edge.
 */
internal fun stepperSettingsSection(): SettingsSection {
    return settingsEntries(
        id = "stepper",
        title = "Stepper",
        order = 20,
        config = Stepper.config,
        update = Stepper::updateConfig,
    ) {
        visibilityEntries()
        pauseEntries()
        matchEntries()
        limitEntries()
    }
}

private fun SettingsEntriesScope<Stepper.Config>.visibilityEntries() {
    toggle(
        key = "enabled",
        title = "Enabled",
        description = "Enable or disable the stepper",
        read = { it.enabled },
        write = { copy(enabled = it) },
    )
    toggle(
        key = "overlayVisible",
        title = "Show floating overlay",
        description = "Hides the floating indicator without disabling capture or pausing",
        enabledWhen = { it.enabled },
        read = { it.overlayVisible },
        write = { copy(overlayVisible = it) },
    )
}

private fun SettingsEntriesScope<Stepper.Config>.pauseEntries() {
    toggle(
        key = "paused",
        title = "Pause",
        description = "Hold logs until you step through them",
        enabledWhen = { it.enabled && it.overlayVisible },
        read = { it.paused },
        write = { copy(paused = it) },
    )
    enum(
        key = "autoResumeSeconds",
        title = "Auto-resume",
        options = AutoResumeDelay.entries,
        noneLabel = "Off",
        enabledWhen = { it.enabled },
        read = { AutoResumeDelay.fromSecondsOrNull(it.autoResumeSeconds) },
        write = { copy(autoResumeSeconds = it?.seconds) },
    )
}

private fun SettingsEntriesScope<Stepper.Config>.matchEntries() {
    toggle(
        key = "pauseOnMatch",
        title = "Pause on match",
        description = "Pause only when a log matches the filter",
        read = { it.pauseOnMatch },
        write = { copy(pauseOnMatch = it) },
    )
    tags(
        key = "pauseOnTags",
        title = "Pause on tags",
        enabledWhen = { it.pauseOnMatch },
        read = { it.pauseOnTags },
        write = { copy(pauseOnTags = it) },
    )
    enum(
        key = "pauseOnLevelAtLeast",
        title = "Pause level at least",
        options = LogLevel.entries.filter { it != LogLevel.None },
        noneLabel = "Any",
        enabledWhen = { it.pauseOnMatch },
        read = { it.pauseOnLevelAtLeast },
        write = { copy(pauseOnLevelAtLeast = it) },
    )
}

private fun SettingsEntriesScope<Stepper.Config>.limitEntries() {
    int(
        key = "maxSteppedEventCount",
        title = "Max stepped events",
        min = 1,
        max = 500,
        read = { it.maxSteppedEventCount },
        write = { copy(maxSteppedEventCount = it) },
    )
}
