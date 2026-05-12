package az.theternal.console.ui.designsystem.foundation.theme

import androidx.compose.runtime.Composable
import az.theternal.console.ui.designsystem.foundation.dimension.Dimens
import az.theternal.console.ui.designsystem.foundation.dimension.LocalMetrics
import az.theternal.console.ui.designsystem.foundation.dimension.Metrics
import az.theternal.console.ui.designsystem.foundation.shape.Rounding
import az.theternal.console.ui.designsystem.foundation.typography.Typography

sealed class Theme {
    abstract val colors: ThemeColors

    companion object {
        val default = DefaultTheme()

        val colors: ThemeColors @Composable get() {
            return LocalThemeColors.current
        }

        val metrics: Metrics @Composable get() {
            return LocalMetrics.current
        }

        val typography = Typography
        val dimens = Dimens
        val rounding = Rounding
    }
}
