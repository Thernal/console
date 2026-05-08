package az.theternal.console.ui.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ThemeColors(
    // Content (text/icon) — decreasing contrast
    val content1: Color,
    val content2: Color,
    val content3: Color,
    val content4: Color,

    // Primary accent
    val primary: Color,
    val primaryMuted: Color,
    val primarySubtle: Color,
    val primaryContent: Color,

    // Backgrounds — increasing elevation
    val background: Color,
    val backgroundRaised: Color,
    val backgroundElevated: Color,

    // Helper states
    val success: Color,
    val successContent: Color,
    val info: Color,
    val infoContent: Color,
    val warning: Color,
    val warningContent: Color,
    val danger: Color,
    val dangerContent: Color,

    // Structural
    val border: Color,
)

val consoleThemeColors = ThemeColors(
    content1 = BrandColors.Neutral.Shade100,
    content2 = BrandColors.Neutral.Shade200,
    content3 = BrandColors.Neutral.Shade300,
    content4 = BrandColors.Neutral.Shade400,

    primary = BrandColors.Blue.Shade400,
    primaryMuted = BrandColors.Blue.Shade500,
    primarySubtle = BrandColors.Blue.Shade900,
    primaryContent = Color.White,

    background = BrandColors.Neutral.Shade950,
    backgroundRaised = BrandColors.Neutral.Shade900,
    backgroundElevated = BrandColors.Neutral.Shade800,

    success = BrandColors.Green.Shade400,
    successContent = BrandColors.Green.Shade900,
    info = BrandColors.Cyan.Shade400,
    infoContent = BrandColors.Cyan.Shade900,
    warning = BrandColors.Amber.Shade400,
    warningContent = BrandColors.Amber.Shade900,
    danger = BrandColors.Red.Shade400,
    dangerContent = BrandColors.Red.Shade900,

    border = BrandColors.Neutral.Shade700,
)
