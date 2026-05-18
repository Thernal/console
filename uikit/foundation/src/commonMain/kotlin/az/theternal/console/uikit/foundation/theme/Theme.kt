package az.theternal.console.uikit.foundation.theme

import androidx.compose.runtime.Composable
import az.theternal.console.uikit.foundation.color.Opacity
import az.theternal.console.uikit.foundation.dimension.Dimens
import az.theternal.console.uikit.foundation.dimension.LocalMetrics
import az.theternal.console.uikit.foundation.dimension.Metrics
import az.theternal.console.uikit.foundation.shape.Rounding
import az.theternal.console.uikit.foundation.typography.Typography

sealed interface Theme {
    val colors: ThemeColors

    companion object {
        val default = DefaultTheme()

        val colors: ThemeColors
            @Composable get() = LocalThemeColors.current

        val metrics: Metrics
            @Composable get() = LocalMetrics.current

        val typography = Typography
        val dimens = Dimens
        val rounding = Rounding
        val opacity = Opacity
    }
}
