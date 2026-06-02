package io.thernal.console.designsystem.foundation.theme

import androidx.compose.runtime.Composable
import io.thernal.console.designsystem.foundation.color.Opacity
import io.thernal.console.designsystem.foundation.dimension.Dimens
import io.thernal.console.designsystem.foundation.dimension.LocalMetrics
import io.thernal.console.designsystem.foundation.dimension.Metrics
import io.thernal.console.designsystem.foundation.shape.Rounding
import io.thernal.console.designsystem.foundation.typography.Typography

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
