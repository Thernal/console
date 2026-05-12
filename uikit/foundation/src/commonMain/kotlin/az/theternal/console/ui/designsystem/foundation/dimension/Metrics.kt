package az.theternal.console.ui.designsystem.foundation.dimension

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp

data class Metrics(
    val iconXs: Dp = Dimens.dp14,
    val iconSm: Dp = Dimens.dp16,
    val iconMd: Dp = Dimens.dp20,
    val iconLg: Dp = Dimens.dp24,
    val iconXl: Dp = Dimens.dp32,
    val borderWidth: Dp = Dimens.dp1,
    val dividerHeight: Dp = Dimens.dp0_5,
    val statusDotSize: Dp = Dimens.dp8,
    val minTouchTarget: Dp = Dimens.dp44,
)

val LocalMetrics = staticCompositionLocalOf { Metrics() }
