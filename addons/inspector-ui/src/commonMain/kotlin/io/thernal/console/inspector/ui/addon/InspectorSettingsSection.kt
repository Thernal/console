package io.thernal.console.inspector.ui.addon

import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorConfig
import io.thernal.console.inspector.InspectorFontScale
import io.thernal.console.inspector.InspectorLayoutDirection
import io.thernal.console.settings.SettingsEntriesScope
import io.thernal.console.settings.SettingsRegistry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries

/**
 * Registered directly against [SettingsRegistry] rather than through a `ConsoleAddon.settings()`
 * hook — same reasoning as `CrashReportAddon`/`NetworkAddon`/`StepperAddon`: keeps `console-api`
 * free of a `settings-api` dependency edge.
 */
internal fun inspectorSettingsSection(): SettingsSection {
    return settingsEntries(
        id = "inspector",
        title = "Inspector",
        order = 50,
        config = ConsoleInspector.config,
        update = ConsoleInspector::updateConfig,
    ) {
        generalEntries()
        recompositionEntries()
        overlayEntries()
        treeEntries()
        environmentEntries()
    }
}

private fun SettingsEntriesScope<InspectorConfig>.generalEntries() {
    toggle(
        key = "enabled",
        title = "Enabled",
        description = "Turns the whole inspector on or off; the app UI is unaffected either way",
        read = { it.enabled },
        write = { copy(enabled = it) },
    )
    toggle(
        key = "showQuickControls",
        title = "Show quick controls",
        description = "Keeps the inspector in the floating dock with shortcuts for the most used actions",
        enabledWhen = { it.enabled },
        read = { it.showQuickControls },
        write = { copy(showQuickControls = it) },
    )
}

private fun SettingsEntriesScope<InspectorConfig>.recompositionEntries() {
    toggle(
        key = "trackRecompositions",
        title = "Track recompositions",
        description = "Counts recompositions and records why they happened",
        enabledWhen = { it.enabled },
        read = { it.trackRecompositions },
        write = { copy(trackRecompositions = it) },
    )
    toggle(
        key = "highlightRecompositions",
        title = "Highlight recompositions",
        description = "Flashes a composable's bounds when it recomposes",
        enabledWhen = { it.enabled && it.trackRecompositions },
        read = { it.highlightRecompositions },
        write = { copy(highlightRecompositions = it) },
    )
    int(
        key = "highlightDurationMillis",
        title = "Highlight duration (ms)",
        min = InspectorConfig.MIN_HIGHLIGHT_DURATION_MILLIS,
        max = InspectorConfig.MAX_HIGHLIGHT_DURATION_MILLIS,
        enabledWhen = { it.enabled && it.trackRecompositions && it.highlightRecompositions },
        read = { it.highlightDurationMillis },
        write = { copy(highlightDurationMillis = it) },
    )
    toggle(
        key = "showHeatmap",
        title = "Recomposition heatmap",
        description = "Tints composables from blue to red by recomposition count",
        enabledWhen = { it.enabled && it.trackRecompositions },
        read = { it.showHeatmap },
        write = { copy(showHeatmap = it) },
    )
    int(
        key = "heatmapThreshold",
        title = "Heatmap threshold",
        min = InspectorConfig.MIN_HEATMAP_THRESHOLD,
        max = InspectorConfig.MAX_HEATMAP_THRESHOLD,
        description = "Recomposition count that counts as fully hot",
        enabledWhen = { it.enabled && it.trackRecompositions && it.showHeatmap },
        read = { it.heatmapThreshold },
        write = { copy(heatmapThreshold = it) },
    )
    toggle(
        key = "showRecompositionCounters",
        title = "Show recomposition counters",
        enabledWhen = { it.enabled && it.trackRecompositions },
        read = { it.showRecompositionCounters },
        write = { copy(showRecompositionCounters = it) },
    )
}

private fun SettingsEntriesScope<InspectorConfig>.overlayEntries() {
    toggle(
        key = "showBounds",
        title = "Show component bounds",
        enabledWhen = { it.enabled },
        read = { it.showBounds },
        write = { copy(showBounds = it) },
    )
    toggle(
        key = "showLabels",
        title = "Show component labels",
        enabledWhen = { it.enabled },
        read = { it.showLabels },
        write = { copy(showLabels = it) },
    )
    toggle(
        key = "showMeasurements",
        title = "Show measurements",
        description = "Distances to the parent around the selected component",
        enabledWhen = { it.enabled },
        read = { it.showMeasurements },
        write = { copy(showMeasurements = it) },
    )
}

private fun SettingsEntriesScope<InspectorConfig>.treeEntries() {
    toggle(
        key = "hideFrameworkNodes",
        title = "Hide framework nodes",
        description = "Hides well-known Compose library composables from the tree, overlay and ranking",
        enabledWhen = { it.enabled },
        read = { it.hideFrameworkNodes },
        write = { copy(hideFrameworkNodes = it) },
    )
    tags(
        key = "appSourceTags",
        title = "App source filters",
        description = "File or function name fragments that mark app code; everything else counts as framework",
        enabledWhen = { it.enabled },
        read = { it.appSourceTags },
        write = { copy(appSourceTags = it) },
    )
    int(
        key = "autoRefreshSeconds",
        title = "Auto-refresh (seconds)",
        min = 0,
        max = InspectorConfig.MAX_AUTO_REFRESH_SECONDS,
        description = "Rebuilds the tree periodically; 0 means manual refresh only",
        enabledWhen = { it.enabled },
        read = { it.autoRefreshSeconds },
        write = { copy(autoRefreshSeconds = it) },
    )
}

private fun SettingsEntriesScope<InspectorConfig>.environmentEntries() {
    enum(
        key = "fontScale",
        title = "Font scale override",
        options = InspectorFontScale.entries,
        noneLabel = "Follow system",
        enabledWhen = { it.enabled },
        read = { it.fontScale },
        write = { copy(fontScale = it) },
    )
    enum(
        key = "layoutDirection",
        title = "Layout direction override",
        options = InspectorLayoutDirection.entries,
        noneLabel = "Follow app",
        enabledWhen = { it.enabled },
        read = { it.layoutDirection },
        write = { copy(layoutDirection = it) },
    )
}
