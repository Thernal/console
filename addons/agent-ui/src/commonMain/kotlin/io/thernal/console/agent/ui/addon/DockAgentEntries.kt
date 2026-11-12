package io.thernal.console.agent.ui.addon

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.agentEntries
import io.thernal.console.agent.enum
import io.thernal.console.api.addon.ConsoleDock
import io.thernal.console.api.addon.ConsoleDockCommand
import io.thernal.console.api.addon.ConsoleDockEdge
import io.thernal.console.api.addon.ConsoleDockLayout
import io.thernal.console.api.addon.ConsoleDockVertical
import kotlinx.coroutines.delay

private const val SETTLE_STEP_MS = 50L
private const val MAX_SETTLE_STEPS = 60
private const val UNCHANGED_STEPS = 10

/**
 * What the agent addon shares about Console's floating dock, which UI tests share the screen with: where it is, and
 * moving it out of the way. Showing and hiding it stay in the Settings section `dock`. Nothing here is saved.
 */
internal fun dockAgentEntries(): AgentEntries {
    return agentEntries(
        id = "dock",
        title = "Floating dock",
        about = "Console's floating widget dock: where it is on screen, and moving it out of the way of a test",
    ) {
        tool(
            name = "dock_state",
            description = "Whether the dock is on screen, its edge, height and bounds in window pixels, minimized or " +
                "open on which widget, and every widget with its hidden flag",
        ) {
            run { currentDockState() }
        }
        action(
            name = "dock_move",
            description = "Moves the dock flush with an edge at a height, out of the way of what a test taps. " +
                "Not saved; a drag by hand moves it again",
        ) {
            val edge = enum(key = "edge", description = "Side of the window", default = ConsoleDockEdge.Left)
            val vertical = enum(key = "vertical", description = "Height", default = ConsoleDockVertical.Bottom)
            run { call -> sendToDock(ConsoleDockCommand.MoveTo(call[edge], call[vertical])) }
        }
        action(name = "dock_minimize", description = "Folds the dock into its button, or opens its card") {
            val minimized = toggle(key = "minimized", description = "true folds, false opens", default = true)
            run { call -> sendToDock(ConsoleDockCommand.SetMinimized(call[minimized])) }
        }
        action(name = "dock_select", description = "Opens the dock's card on a widget's tab") {
            val widget = text(key = "widget", description = "Widget id or title, as `dock_state` lists them")
            run { call -> sendToDock(ConsoleDockCommand.SelectWidget(dockWidgetId(call[widget]))) }
        }
    }
}

private fun currentDockState(): String {
    return dockStateText(
        config = ConsoleDock.config.value,
        layout = ConsoleDock.layout.value,
        widgetTitles = ConsoleDock.widgets.associate { it.id to it.title },
    )
}

private suspend fun sendToDock(command: ConsoleDockCommand): String {
    if (ConsoleDock.layout.value == null) {
        val widgetTitles = ConsoleDock.widgets.associate { it.id to it.title }
        error("No dock on screen. ${noDockReason(ConsoleDock.config.value, widgetTitles)}")
    }
    val before = ConsoleDock.layout.value
    ConsoleDock.dispatch(command)
    settle(before)
    return currentDockState()
}

/**
 * Waits until the dock has taken the command and stopped moving, so the answer shows where a move or a fold ended.
 * A command that changes nothing (the dock is already there) is given [UNCHANGED_STEPS] to show up.
 */
private suspend fun settle(before: ConsoleDockLayout?) {
    repeat(MAX_SETTLE_STEPS) { step ->
        delay(SETTLE_STEP_MS)
        val now = ConsoleDock.layout.value ?: return
        if (!now.isMoving && (now != before || step >= UNCHANGED_STEPS)) return
    }
}

/** The id of the widget [name] names, by id or title, ignoring case. */
private fun dockWidgetId(name: String): String {
    val widget = ConsoleDock.widgets.firstOrNull { it.id.equals(name.trim(), ignoreCase = true) }
        ?: ConsoleDock.widgets.firstOrNull { it.title.equals(name.trim(), ignoreCase = true) }
    requireNotNull(widget) {
        "No dock widget \"$name\". Widgets: ${ConsoleDock.widgets.joinToString { it.id }.ifEmpty { "none" }}"
    }
    require(widget.id !in ConsoleDock.config.value.hiddenWidgetIds) {
        "Widget ${widget.id} is hidden (settings section \"dock\", key \"hiddenWidgetIds\")"
    }
    return widget.id
}
