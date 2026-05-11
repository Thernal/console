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
import az.theternal.console.ui.designsystem.DsTheme
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle
import kotlin.math.abs

@Composable
internal fun Log.logAccentColor(): Color {
    return with(DsTheme.colors) {
        level?.let {
            return@with when (it) {
                LogLevel.Verbose -> content3
                LogLevel.Debug -> primary
                LogLevel.Info -> info
                LogLevel.Success -> success
                LogLevel.Warning -> warning
                LogLevel.Error -> danger
                LogLevel.Fatal -> fatal
            }
        }
        val palette = listOf(primary, success, warning, info, danger)
        val tag = this@logAccentColor.tag ?: return@with content3
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
            .clip(DsTheme.rounding.xs)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = DsTheme.dimens.xs, vertical = DsTheme.dimens.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xxs),
    ) {
        Box(
            modifier = Modifier
                .size(DsTheme.metrics.statusDotSize)
                .clip(CircleShape)
                .background(color),
        )
        if (tag != null) {
            DsText(text = tag, style = DsTextStyle.LabelSmall, color = color)
        }
    }
}
