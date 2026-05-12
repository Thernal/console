package az.theternal.console.ui.designsystem.components.product.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.foundation.theme.Theme

data class DsButtonStyle(
    val background: Color,
    val content: Color,
    val border: Color,
) {
    companion object {
        val primary: DsButtonStyle
            @Composable get() = DsButtonStyle(
                background = Theme.colors.primary01,
                content = Theme.colors.primaryContent,
                border = Color.Transparent,
            )

        val danger: DsButtonStyle
            @Composable get() = DsButtonStyle(
                background = Theme.colors.danger,
                content = Theme.colors.dangerContent,
                border = Color.Transparent,
            )

        val secondary: DsButtonStyle
            @Composable get() = DsButtonStyle(
                background = Theme.colors.background2,
                content = Theme.colors.content01,
                border = Color.Transparent,
            )

        val outlined: DsButtonStyle
            @Composable get() = DsButtonStyle(
                background = Color.Transparent,
                content = Theme.colors.primary01,
                border = Theme.colors.primary01,
            )

        val secondaryOutlined: DsButtonStyle
            @Composable get() = DsButtonStyle(
                background = Color.Transparent,
                content = Theme.colors.content01,
                border = Theme.colors.border,
            )
    }
}
