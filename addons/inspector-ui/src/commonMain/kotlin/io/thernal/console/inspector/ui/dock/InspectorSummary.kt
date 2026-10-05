package io.thernal.console.inspector.ui.dock

import io.thernal.console.inspector.InspectorConfig
import io.thernal.console.inspector.InspectorLayoutDirection

/** The features currently switched on, in the order the card lists them. Empty when the inspector is at rest. */
internal fun inspectorActiveFeatures(
    config: InspectorConfig,
    isFrozen: Boolean,
): List<String> = buildList {
    if (isFrozen) add("Frozen")
    if (config.showBounds) add("Bounds")
    if (config.showLabels) add("Labels")
    if (config.showHeatmap) add("Heatmap")
    if (config.showRecompositionCounters) add("Counters")
    config.fontScale?.let { add("Font ×${it.factor}") }
    if (config.layoutDirection == InspectorLayoutDirection.Rtl) add("RTL")
}

/** The live line in the panel header: what is being picked, what is switched on, or that the inspector is ready. */
internal fun inspectorSummary(
    isInspecting: Boolean,
    selectedName: String?,
    activeFeatures: List<String>,
    measureText: String? = null,
): String = when {
    isInspecting && measureText != null -> measureText
    isInspecting -> selectedName?.let { "Picking · $it" } ?: "Tap or drag to pick"
    activeFeatures.isEmpty() -> "Ready"
    else -> activeFeatures.joinToString(" · ")
}

/** The live line while measuring: what to tap next, or the distance between the two items. */
internal fun measureSummary(
    anchorName: String?,
    targetName: String?,
    distance: String?,
): String = when {
    anchorName == null -> "Measure · tap the first item"
    targetName == null || distance == null -> "Measure · $anchorName → tap the second"
    else -> "$anchorName → $targetName · $distance"
}
