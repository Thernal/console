package az.theternal.console.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.designsystem.foundation.theme.ThemeColors
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

@Composable
fun Log.logAccentColor(): Color {
    val colors = Theme.colors
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
