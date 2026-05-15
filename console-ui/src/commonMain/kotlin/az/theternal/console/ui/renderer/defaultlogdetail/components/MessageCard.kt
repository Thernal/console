package az.theternal.console.ui.renderer.defaultlogdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import az.theternal.console.runtime.model.Log
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.foundation.theme.Theme

private val accentBarWidth = Theme.dimens.dp3

@Composable
internal fun MessageCard(
    log: Log,
    accentColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background2)
            .border(Theme.metrics.borderWidth, accentColor.copy(alpha = 0.35f), Theme.rounding.r12),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(accentBarWidth)
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
