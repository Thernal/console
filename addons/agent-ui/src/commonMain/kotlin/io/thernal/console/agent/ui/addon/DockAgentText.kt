package io.thernal.console.agent.ui.addon

import io.thernal.console.api.addon.ConsoleDockConfig
import io.thernal.console.api.addon.ConsoleDockLayout
import kotlin.math.abs
import kotlin.math.roundToInt

private const val PERCENT = 100
private const val NAMED_HEIGHT_TOLERANCE = 0.02f
private const val CENTER = 0.5f
private const val ALL_HIDDEN = "No widget is shown in it: each is hidden (settings section \"dock\", key " +
    "\"hiddenWidgetIds\") or has nothing to show yet."
private const val SHOW_DOCK = "settings_set(section=\"dock\", key=\"isVisible\", value=\"true\")"

/** The `dock_state` answer: where the dock is, what it shows, and every widget with its hidden flag. */
internal fun dockStateText(
    config: ConsoleDockConfig,
    layout: ConsoleDockLayout?,
    widgetTitles: Map<String, String>,
): String {
    val lines = mutableListOf<String>()
    lines += if (layout == null) "No dock on screen. ${noDockReason(config, widgetTitles)}" else placeText(layout)
    lines += "Dock visible in settings (section \"dock\", key \"isVisible\"): ${if (config.isVisible) "yes" else "no"}"
    lines += if (widgetTitles.isEmpty()) "Widgets: none installed" else "Widgets:"
    lines += widgetTitles.map { (id, title) ->
        val hidden = if (id in config.hiddenWidgetIds) " (hidden)" else ""
        "- $id \"$title\"$hidden"
    }
    return lines.joinToString(separator = "\n")
}

/** Why no dock is on screen, with what brings it back. */
internal fun noDockReason(
    config: ConsoleDockConfig,
    widgetTitles: Map<String, String>,
): String {
    return when {
        !config.isVisible -> "It is hidden; $SHOW_DOCK shows it."
        widgetTitles.isEmpty() -> "No addon with a dock widget is installed."
        else -> ALL_HIDDEN
    }
}

private fun placeText(layout: ConsoleDockLayout): String {
    val form = if (layout.isMinimized) "minimized" else "open on widget ${layout.selectedWidgetId}"
    return "Dock on screen: ${layout.edge.name.lowercase()} edge, ${heightText(layout.verticalFraction)}, $form.\n" +
        "Bounds in window pixels: left ${layout.left}, top ${layout.top}, ${layout.width}x${layout.height}"
}

private fun heightText(fraction: Float): String {
    return when {
        fraction <= NAMED_HEIGHT_TOLERANCE -> "top"
        fraction >= 1f - NAMED_HEIGHT_TOLERANCE -> "bottom"
        abs(fraction - CENTER) <= NAMED_HEIGHT_TOLERANCE -> "center"
        else -> "${(fraction * PERCENT).roundToInt()}% down"
    }
}
