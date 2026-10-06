package io.thernal.console.inspector

/**
 * Runtime configuration of the UI inspector.
 *
 * Recomposition tracking, highlighting and the heatmap only have an effect while [enabled] is
 * true. [showQuickControls] puts the inspector in the floating dock; it is on by default and a pick keeps the
 * dock on screen even when it is off. [fontScale] and [layoutDirection] override the environment of the host app
 * content.
 */
data class InspectorConfig(
    val enabled: Boolean = true,
    val showQuickControls: Boolean = true,
    val trackRecompositions: Boolean = true,
    val highlightRecompositions: Boolean = false,
    val highlightDurationMillis: Int = DEFAULT_HIGHLIGHT_DURATION_MILLIS,
    val showHeatmap: Boolean = false,
    val heatmapThreshold: Int = DEFAULT_HEATMAP_THRESHOLD,
    val showBounds: Boolean = false,
    val showLabels: Boolean = false,
    val showRecompositionCounters: Boolean = false,
    val showMeasurements: Boolean = true,
    val hideFrameworkNodes: Boolean = true,
    val appSourceTags: Set<String> = emptySet(),
    val autoRefreshSeconds: Int = 0,
    val fontScale: InspectorFontScale? = null,
    val layoutDirection: InspectorLayoutDirection? = null,
) {
    internal fun normalized(): InspectorConfig {
        return copy(
            highlightDurationMillis = highlightDurationMillis.coerceIn(
                MIN_HIGHLIGHT_DURATION_MILLIS,
                MAX_HIGHLIGHT_DURATION_MILLIS,
            ),
            heatmapThreshold = heatmapThreshold.coerceIn(MIN_HEATMAP_THRESHOLD, MAX_HEATMAP_THRESHOLD),
            autoRefreshSeconds = autoRefreshSeconds.coerceIn(0, MAX_AUTO_REFRESH_SECONDS),
        )
    }

    companion object {
        const val DEFAULT_HIGHLIGHT_DURATION_MILLIS = 600
        const val MIN_HIGHLIGHT_DURATION_MILLIS = 100
        const val MAX_HIGHLIGHT_DURATION_MILLIS = 5_000
        const val DEFAULT_HEATMAP_THRESHOLD = 30
        const val MIN_HEATMAP_THRESHOLD = 1
        const val MAX_HEATMAP_THRESHOLD = 1_000
        const val MAX_AUTO_REFRESH_SECONDS = 60
    }
}
