package az.theternal.console.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.extensions.from
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun DsTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    singleLine: Boolean = true,
    prefix: (@Composable RowScope.() -> Unit)? = null,
    suffix: (@Composable RowScope.() -> Unit)? = null,
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
                prefix?.invoke(this)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = Theme.dimens.dp12),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.text.isEmpty()) {
                        DsText(
                            text = hint,
                            style = Theme.typography.body02,
                            color = Theme.colors.content04,
                        )
                    }
                    innerTextField()
                }

                suffix?.invoke(this)
            }
        },
    )
}

@DsPreview
@Composable
private fun PreviewDsTextField() {
    ThemeProvider {
        Column(
            modifier = Modifier.padding(Theme.dimens.dp16),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsTextField(
                value = TextFieldValue(""),
                onValueChange = {},
                hint = "Search…",
            )
            DsTextField(
                value = TextFieldValue.from("some input text"),
                onValueChange = {},
            )
        }
    }
}
