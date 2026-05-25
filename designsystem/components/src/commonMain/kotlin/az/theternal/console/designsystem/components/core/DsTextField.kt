package az.theternal.console.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun DsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    singleLine: Boolean = true,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        textStyle = Theme.typography.body02.copy(color = Theme.colors.content01),
        cursorBrush = SolidColor(Theme.colors.primary01),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = Theme.metrics.minTouchTarget)
                    .background(Theme.colors.background2, Theme.rounding.r12)
                    .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r12)
                    .padding(horizontal = Theme.dimens.dp12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                prefix?.invoke()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = Theme.dimens.dp12),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        DsText(
                            text = hint,
                            style = Theme.typography.body02,
                            color = Theme.colors.content04,
                        )
                    }
                    innerTextField()
                }

                suffix?.invoke()
            }
        },
    )
}
