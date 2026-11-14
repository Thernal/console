package io.thernal.console.inspector.ui.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.thernal.console.designsystem.foundation.theme.Theme

/** The overlay's colors and label text from the theme, for the draw layer and the agent's snapshots. */
@Composable
internal fun rememberInspectorDrawStyle(): InspectorDrawStyle {
    val colors = Theme.colors
    val labelText = Theme.typography.label03
    return remember(colors, labelText) {
        InspectorDrawStyle(
            bounds = colors.info,
            flash = colors.warning,
            selection = colors.primary01,
            anchor = colors.success,
            padding = colors.warning,
            measurement = colors.danger,
            labelBackground = colors.background1,
            labelText = labelText.copy(color = colors.content01),
            heat = HeatColors(cold = colors.info, warm = colors.warning, hot = colors.danger),
        )
    }
}
