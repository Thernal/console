package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsTextField
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun TagInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    DsTextField(
        value = value,
        onValueChange = onValueChange,
        hint = "Add tag…",
        suffix = {
            if (value.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(Theme.dimens.dp32)
                        .clip(Theme.rounding.r8)
                        .background(Theme.colors.primary01)
                        .pressable(onPress = onAdd),
                    contentAlignment = Alignment.Center,
                ) {
                    DsIcon(
                        icon = Icons.Outlined.Add,
                        size = Theme.metrics.iconSm,
                        color = Theme.colors.primaryContent,
                    )
                }
            }
        },
    )
}

@DsPreview
@Composable
private fun PreviewTagInputFieldEmpty() {
    ThemeProvider {
        TagInputField(
            value = "",
            onValueChange = {},
            onAdd = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewTagInputFieldFilled() {
    ThemeProvider {
        TagInputField(
            value = "network",
            onValueChange = {},
            onAdd = {},
        )
    }
}
