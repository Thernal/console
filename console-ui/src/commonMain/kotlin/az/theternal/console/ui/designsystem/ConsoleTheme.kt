package az.theternal.console.ui.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.foundation.DsTypography
import az.theternal.console.ui.designsystem.foundation.ThemeColors
import az.theternal.console.ui.designsystem.foundation.consoleThemeColors

internal fun buildColorScheme(c: ThemeColors): ColorScheme {
    return darkColorScheme(
        primary = c.primary,
        onPrimary = c.primaryContent,
        primaryContainer = c.primary3,
        onPrimaryContainer = c.primary,
        background = c.background1,
        onBackground = c.content1,
        surface = c.background1,
        onSurface = c.content1,
        surfaceVariant = c.background2,
        onSurfaceVariant = c.content3,
        surfaceContainer = c.background2,
        surfaceContainerHigh = c.background3,
        outline = c.border,
        outlineVariant = c.border,
        error = c.danger,
        onError = c.dangerContent,
        scrim = Color.Black.copy(alpha = 0.6f),
    )
}

internal fun buildTypography(): Typography {
    return Typography(
        bodySmall = DsTypography.bodySmall,
        bodyMedium = DsTypography.body,
        bodyLarge = DsTypography.bodyLarge,
        labelSmall = DsTypography.labelSmall,
        labelMedium = DsTypography.labelMedium,
        titleSmall = DsTypography.titleSmall,
        titleMedium = DsTypography.titleMedium,
    )
}

@Composable
internal fun ConsoleTheme(content: @Composable () -> Unit) {
    val colors = consoleThemeColors
    CompositionLocalProvider(LocalDsColors provides colors) {
        MaterialTheme(
            colorScheme = buildColorScheme(colors),
            typography = buildTypography(),
            content = content,
        )
    }
}
