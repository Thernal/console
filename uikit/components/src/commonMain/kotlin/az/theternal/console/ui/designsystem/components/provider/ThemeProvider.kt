package az.theternal.console.ui.designsystem.components.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import az.theternal.console.ui.designsystem.components.core.LocalDsContentColor
import az.theternal.console.ui.designsystem.components.core.LocalDsTextStyle
import az.theternal.console.ui.designsystem.foundation.theme.LocalThemeColors
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun ThemeProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalDsContentColor provides Theme.default.colors.content01,
        LocalThemeColors provides Theme.default.colors,
        LocalDsTextStyle provides Theme.typography.body02,
        content = content,
    )
}
