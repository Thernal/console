package io.thernal.console.api.addon

/** A named height for the dock, as a [fraction] of the room it can travel: 0 at the top, 1 at the bottom. */
enum class ConsoleDockVertical(val fraction: Float) {
    Top(fraction = 0f),
    Center(fraction = 0.5f),
    Bottom(fraction = 1f),
}
