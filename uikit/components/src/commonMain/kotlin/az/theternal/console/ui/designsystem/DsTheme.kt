package az.theternal.console.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import az.theternal.console.ui.designsystem.foundation.Dimens
import az.theternal.console.ui.designsystem.foundation.DsTypography
import az.theternal.console.ui.designsystem.foundation.Metrics
import az.theternal.console.ui.designsystem.foundation.Rounding
import az.theternal.console.ui.designsystem.foundation.ThemeColors
import az.theternal.console.ui.designsystem.foundation.consoleThemeColors

val LocalDsColors = staticCompositionLocalOf { consoleThemeColors }

object DsTheme {
    val colors: ThemeColors
        @Composable get() = LocalDsColors.current
    val typography = DsTypography
    val rounding = Rounding
    val dimens = Dimens
    val metrics = Metrics
}
