package io.thernal.console.inspector.ui.overlay

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/** Colors and text style resolved from the theme once per composition, so the draw lambda stays allocation-free. */
internal class InspectorDrawStyle(
    val bounds: Color,
    val flash: Color,
    val selection: Color,
    val anchor: Color,
    val padding: Color,
    val measurement: Color,
    val labelBackground: Color,
    val labelText: TextStyle,
    val heat: HeatColors,
)
