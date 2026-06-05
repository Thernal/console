package io.thernal.console.designsystem.components.modifier

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import io.thernal.console.designsystem.foundation.theme.Theme

fun Modifier.focusRing(
    shape: Shape? = null,
    color: Color? = null,
    width: Dp? = null,
): Modifier {
    return composed {
        this@focusRing.border(
            shape = shape ?: Theme.rounding.r12,
            color = color ?: Theme.colors.warning,
            width = width ?: Theme.metrics.borderWidth,
        )
    }
}
