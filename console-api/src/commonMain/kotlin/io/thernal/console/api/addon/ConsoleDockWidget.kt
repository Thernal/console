package io.thernal.console.api.addon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A widget an addon places in the console dock: one shared floating container that owns position,
 * dragging and stacking, so addons never fight over screen corners.
 *
 * Resting, the dock is a single round button, so no addon looks more important than another. Tapping it opens a
 * card with one tab per widget, showing the selected widget's [summary] and [Panel]. When a widget's [status] is
 * [ConsoleDockStatus.Attention] the button grows a pill beside it with that widget's [QuickAction], so the one
 * control the user needs is always within reach; nothing else is shown while the dock is folded.
 *
 * Every `@Composable` member is read inside the dock's composition, so it may observe state directly.
 */
@Stable
interface ConsoleDockWidget {
    /** Stable, unique key. Also what users list under "hidden widgets" in settings. */
    val id: String

    /** The widget's name, shown on its tab. */
    val title: String

    /** The tab's glyph. */
    val icon: ImageVector

    /**
     * Optional position hint, ascending. Leave it unset: widgets with the same order are sorted by
     * [title], which keeps the dock neutral and the same on every platform.
     */
    val order: Int get() = Int.MAX_VALUE

    /** Whether the widget has anything to show at the moment. Unavailable widgets take no space. */
    @Composable
    fun isAvailable(): Boolean = true

    /**
     * The widget's state. [ConsoleDockStatus.Attention] keeps it on screen even when widgets are hidden: a
     * paused pipeline needs its resume control, an active picker its exit button.
     */
    @Composable
    fun status(): ConsoleDockStatus {
        return ConsoleDockStatus.Idle
    }

    /** One short live line above the open panel, such as `Paused · 3 held`. */
    @Composable
    fun summary(): String? = null

    /** What to do right now, shown beside the folded dock only while [status] is Attention. One or two controls. */
    @Composable
    fun QuickAction() {
    }

    /** All the widget's controls, shown when its tab is selected. */
    @Composable
    fun Panel()
}
