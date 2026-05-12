package az.theternal.console.ui.designsystem.foundation.theme

import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.foundation.color.BrandColors

class DefaultTheme : Theme() {
    override val colors: ThemeColors = ThemeColors(
        content01 = BrandColors.neutral.s100,
        content02 = BrandColors.neutral.s200,
        content03 = BrandColors.neutral.s300,
        content04 = BrandColors.neutral.s400,
        primary01 = BrandColors.blue.s400,
        primary02 = BrandColors.blue.s500,
        primary03 = BrandColors.blue.s900,
        primaryContent = Color.White,
        background1 = BrandColors.neutral.s1000,
        background2 = BrandColors.neutral.s900,
        background3 = BrandColors.neutral.s800,
        success = BrandColors.green.s400,
        successContent = BrandColors.green.s900,
        info = BrandColors.cyan.s400,
        infoContent = BrandColors.cyan.s900,
        warning = BrandColors.amber.s400,
        warningContent = BrandColors.amber.s900,
        danger = BrandColors.red.s400,
        dangerContent = BrandColors.red.s900,
        fatal = BrandColors.violet.s400,
        border = BrandColors.neutral.s700,
    )
}
