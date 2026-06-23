package io.thernal.console.sample.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark "premium" palette for the demo host app. Kept local to the sample — the console UI
// has its own theme and is unaffected by this.
val Background = Color(0xFF0E0F13)
val Surface = Color(0xFF16181F)
val SurfaceElevated = Color(0xFF1E212A)
val Outline = Color(0x14FFFFFF)
val OnSurface = Color(0xFFF2F3F5)
val Muted = Color(0xFF8A8F98)
val Brand = Color(0xFF8B7CFF)
val Success = Color(0xFF4ADE80)
val Warning = Color(0xFFFBBF24)

@Composable
fun SampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Brand,
            onPrimary = Color(0xFF12101F),
            background = Background,
            onBackground = OnSurface,
            surface = Surface,
            onSurface = OnSurface,
            surfaceVariant = SurfaceElevated,
            outline = Outline,
        ),
        typography = Typography(),
        content = content,
    )
}
