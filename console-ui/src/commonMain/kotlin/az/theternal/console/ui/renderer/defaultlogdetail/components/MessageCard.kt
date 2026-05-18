package az.theternal.console.ui.renderer.defaultlogdetail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.background
import az.theternal.console.Log
import az.theternal.console.designsystem.components.core.DsCard
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun MessageCard(
    log: Log,
    accentColor: Color,
) {
    DsCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accentColor.copy(alpha = Theme.opacity.S35),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(Theme.dimens.dp3)
                    .fillMaxHeight()
                    .background(accentColor),
            )
            SelectionContainer(modifier = Modifier.weight(1f)) {
                DsText(
                    text = log.message,
                    modifier = Modifier.padding(
                        horizontal = Theme.dimens.dp12,
                        vertical = Theme.dimens.dp12,
                    ),
                    style = Theme.typography.body02.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = Theme.typography.body01.lineHeight,
                    ),
                    color = Theme.colors.content01,
                )
            }
        }
    }
}
