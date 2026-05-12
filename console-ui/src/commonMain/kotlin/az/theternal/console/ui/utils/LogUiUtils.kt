package az.theternal.console.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import az.theternal.console.core.base.Log
import az.theternal.console.core.base.LogLevel
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsText
import kotlin.math.abs

@Composable
internal fun Log.logAccentColor(): Color {
    return with(Theme.colors) {
        level?.let {
            return@with when (it) {
                LogLevel.Verbose -> content03
                LogLevel.Debug -> primary01
                LogLevel.Info -> info
                LogLevel.Success -> success
                LogLevel.Warning -> warning
                LogLevel.Error -> danger
                LogLevel.Fatal -> fatal
            }
        }
        val palette = listOf(primary01, success, warning, info, danger)
        val tag = this@logAccentColor.tag ?: return@with content03
        palette[abs(tag.hashCode()) % palette.size]
    }
}

@Composable
internal fun LogTagBadge(
    tag: String?,
    color: Color,
) {
    Row(
        modifier = Modifier
            .clip(Theme.rounding.r4)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = Theme.dimens.dp4, vertical = Theme.dimens.dp2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
    ) {
        Box(
            modifier = Modifier
                .size(Theme.metrics.statusDotSize)
                .clip(CircleShape)
                .background(color),
        )
        if (tag != null) {
            DsText(text = tag, style = Theme.typography.label02, color = color)
        }
    }
}
