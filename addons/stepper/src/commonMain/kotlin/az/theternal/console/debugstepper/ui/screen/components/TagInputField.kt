package az.theternal.console.debugstepper.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun TagInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.rounding.r8)
            .background(Theme.colors.background3)
            .border(
                width = Theme.metrics.borderWidth,
                color = Theme.colors.border,
                shape = Theme.rounding.r8,
            )
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = Theme.typography.body02.copy(color = Theme.colors.content01),
            cursorBrush = SolidColor(Theme.colors.primary01),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    DsText(
                        text = "Add tag…",
                        style = Theme.typography.body02,
                        color = Theme.colors.content04,
                    )
                }
                inner()
            },
        )
        if (value.isNotBlank()) {
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(Theme.dimens.dp24)
                    .clip(Theme.rounding.r6)
                    .background(Theme.colors.primary01)
                    .pressable(
                        onPress = onAdd,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                DsIcon(
                    icon = Icons.Outlined.Add,
                    size = Theme.metrics.iconSm,
                    color = Theme.colors.primaryContent,
                )
            }
        }
    }
}
