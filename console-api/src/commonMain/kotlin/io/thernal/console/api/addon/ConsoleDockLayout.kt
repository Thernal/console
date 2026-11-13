package io.thernal.console.api.addon

/**
 * Where the dock is and how it looks, as the dock on screen reports it through [ConsoleDock.layout]. Positions are
 * where the dock is going, not where an animation has it this frame.
 *
 * @property edge The nearer side of the window.
 * @property verticalFraction How far down the dock is in the room it can travel: 0 at the top, 1 at the bottom.
 * @property isMinimized Whether the dock is folded into its button (or the pill a widget needing attention grows).
 * @property selectedWidgetId The widget whose tab the card shows.
 * @property left The dock's left edge, in window pixels.
 * @property top The dock's top edge, in window pixels.
 * @property width The dock's width, in pixels.
 * @property height The dock's height, in pixels.
 * @property isMoving Whether the dock is still on its way: it is animating, being dragged, or the screen has not yet
 *   drawn its latest change. Wait for `false` before reading what a command did.
 */
data class ConsoleDockLayout(
    val edge: ConsoleDockEdge,
    val verticalFraction: Float,
    val isMinimized: Boolean,
    val selectedWidgetId: String?,
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
    val isMoving: Boolean = false,
)
