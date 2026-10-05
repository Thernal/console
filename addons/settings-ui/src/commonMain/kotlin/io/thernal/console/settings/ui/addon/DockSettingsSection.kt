package io.thernal.console.settings.ui.addon

import io.thernal.console.api.addon.ConsoleDock
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries

/** Dock-wide visibility, generic for every addon that places a widget in the console dock. */
internal fun dockSettingsSection(): SettingsSection {
    return settingsEntries(
        id = "dock",
        title = "Floating widgets",
        order = DOCK_SECTION_ORDER,
        config = ConsoleDock.config,
        update = ConsoleDock::updateConfig,
    ) {
        toggle(
            key = "isVisible",
            title = "Show floating widgets",
            description = "Hides every dock widget and overlay. Widgets that need attention, " +
                "like a paused stepper, stay visible",
            read = { it.isVisible },
            write = { copy(isVisible = it) },
        )
        tags(
            key = "hiddenWidgetIds",
            title = "Hidden widgets",
            description = "Widget ids to hide individually, for example stepper or inspector",
            enabledWhen = { it.isVisible },
            read = { it.hiddenWidgetIds },
            write = { copy(hiddenWidgetIds = it) },
        )
    }
}

private const val DOCK_SECTION_ORDER = 50
