package io.thernal.console.inspector.ui.dock

import io.thernal.console.inspector.InspectorFontScale

/** Next override when stepping through "follow system" and every scale in turn. */
internal fun nextFontScale(current: InspectorFontScale?): InspectorFontScale? {
    val options = InspectorFontScale.entries
    if (current == null) return options.first()
    return options.getOrNull(options.indexOf(current) + 1)
}
