package io.thernal.console.compose.view.defaultdetail.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.thernal.console.compose.util.LocalSearchQuery
import io.thernal.console.compose.util.buildHighlightedText
import io.thernal.console.runtime.Log
import io.thernal.console.runtime.LogLevel
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun MessageCard(
    log: Log,
    accentColor: Color,
) {
    val query = LocalSearchQuery.current
    val warningBg = Theme.colors.warning
    val warningFg = Theme.colors.warningContent
    val highlightedMessage = remember(log.message, query.value) {
        buildHighlightedText(log.message, query.value, warningBg, warningFg)
    }

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
                    text = highlightedMessage,
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

@DsPreview
@Composable
private fun PreviewMessageCard() {
    ThemeProvider {
        MessageCard(
            log = Log("GET /api/users -> 200 OK", tag = "HTTP", level = LogLevel.Success),
            accentColor = Theme.colors.success,
        )
    }
}
