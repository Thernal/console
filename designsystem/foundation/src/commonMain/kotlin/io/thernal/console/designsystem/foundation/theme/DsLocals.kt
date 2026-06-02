package io.thernal.console.designsystem.foundation.theme

import androidx.compose.runtime.staticCompositionLocalOf

val LocalDsContentColor = staticCompositionLocalOf {
    Theme.default.colors.content01
}

val LocalDsTextStyle = staticCompositionLocalOf {
    Theme.typography.body02
}
