package io.thernal.console.api.addon

/**
 * What the dock and the layered overlays show. Hiding never removes a widget that needs attention.
 */
data class ConsoleDockConfig(
    val isVisible: Boolean = true,
    val hiddenWidgetIds: Set<String> = emptySet(),
) {
    /** Whether the widget [id] (or an unowned overlay when `null`) is on screen under this config. */
    fun shows(
        id: String?,
        needsAttention: Boolean,
    ): Boolean {
        if (needsAttention) return true
        return isVisible && (id == null || id !in hiddenWidgetIds)
    }
}
