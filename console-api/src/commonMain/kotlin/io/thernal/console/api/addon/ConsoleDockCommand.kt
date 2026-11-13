package io.thernal.console.api.addon

/**
 * A request to the dock on screen, sent with [ConsoleDock.dispatch]. The dock clamps every move inside the window,
 * the same way it clamps a drag, and nothing a command changes is saved: the next launch starts where it always does.
 */
sealed interface ConsoleDockCommand {
    /** Puts the dock flush with [edge] at the height [vertical]. */
    data class MoveTo(
        val edge: ConsoleDockEdge,
        val vertical: ConsoleDockVertical,
    ) : ConsoleDockCommand

    /** Folds the dock into its button, or opens its card. */
    data class SetMinimized(val isMinimized: Boolean) : ConsoleDockCommand

    /** Opens the card on the tab of the widget [widgetId]. */
    data class SelectWidget(val widgetId: String) : ConsoleDockCommand
}
