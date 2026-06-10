package io.thernal.console.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.designsystem.foundation.theme.ThemeColors
import kotlin.math.abs

fun LogLevel.themeColor(colors: ThemeColors): Color {
    return with(colors) {
        when (this@themeColor) {
            LogLevel.None -> content03
            LogLevel.Verbose -> content03
            LogLevel.Debug -> primary01
            LogLevel.Info -> info
            LogLevel.Success -> success
            LogLevel.Warning -> warning
            LogLevel.Error -> danger
            LogLevel.Fatal -> fatal
        }
    }
}

fun Log.logAccentColor(colors: ThemeColors): Color {
    return if (level == LogLevel.None) {
        val palette = listOf(
            colors.primary01,
            colors.success,
            colors.warning,
            colors.info,
            colors.danger,
        )
        tag?.let { palette[abs(it.hashCode()) % palette.size] } ?: colors.content03
    } else {
        level.themeColor(colors)
    }
}

@Composable
fun Log.logAccentColor(): Color {
    return logAccentColor(Theme.colors)
}

@Composable
fun tagAccentColor(tag: String): Color {
    val colors = Theme.colors
    val palette = listOf(
        colors.primary01,
        colors.success,
        colors.warning,
        colors.info,
        colors.danger,
    )
    return palette[abs(tag.hashCode()) % palette.size]
}
