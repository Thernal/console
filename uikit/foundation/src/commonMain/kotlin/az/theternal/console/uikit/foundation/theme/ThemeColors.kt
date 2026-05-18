package az.theternal.console.uikit.foundation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ThemeColors(
    val content01: Color,
    val content02: Color,
    val content03: Color,
    val content04: Color,
    val primary01: Color,
    val primary02: Color,
    val primary03: Color,
    val primaryContent: Color,
    val background1: Color,
    val background2: Color,
    val background3: Color,
    val success: Color,
    val successContent: Color,
    val info: Color,
    val infoContent: Color,
    val warning: Color,
    val warningContent: Color,
    val danger: Color,
    val dangerContent: Color,
    val fatal: Color,
    val border: Color,
)

val LocalThemeColors = staticCompositionLocalOf { Theme.default.colors }
