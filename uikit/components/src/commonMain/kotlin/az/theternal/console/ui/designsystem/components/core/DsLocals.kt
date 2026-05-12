package az.theternal.console.ui.designsystem.components.core

import androidx.compose.runtime.staticCompositionLocalOf
import az.theternal.console.ui.designsystem.foundation.theme.Theme

val LocalDsContentColor = staticCompositionLocalOf {
    Theme.default.colors.content01
}

val LocalDsTextStyle = staticCompositionLocalOf {
    Theme.typography.body02
}
