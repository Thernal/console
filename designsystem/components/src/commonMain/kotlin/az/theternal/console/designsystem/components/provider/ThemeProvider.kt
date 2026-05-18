package az.theternal.console.designsystem.components.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import az.theternal.console.designsystem.foundation.theme.LocalDsContentColor
import az.theternal.console.designsystem.foundation.theme.LocalDsTextStyle
import az.theternal.console.designsystem.foundation.theme.LocalThemeColors
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun ThemeProvider(
    theme: Theme = Theme.default,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDsContentColor provides theme.colors.content01,
        LocalThemeColors provides theme.colors,
        LocalDsTextStyle provides Theme.typography.body02,
        content = content,
    )
}
