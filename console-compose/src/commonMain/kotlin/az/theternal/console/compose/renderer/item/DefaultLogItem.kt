package az.theternal.console.compose.renderer.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.compose.components.LogTagBadge
import az.theternal.console.compose.util.LocalSearchQuery
import az.theternal.console.compose.util.buildHighlightedText
import az.theternal.console.compose.util.formatLogTimestamp
import az.theternal.console.compose.util.logAccentColor
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun DefaultLogItem(
    log: Log,
    onClick: () -> Unit,
) {
    val accentColor = log.logAccentColor()
    val query = LocalSearchQuery.current
    val warningBg = Theme.colors.warning
    val warningFg = Theme.colors.warningContent
    val highlightedMessage = remember(log.message, query) {
        buildHighlightedText(log.message, query, warningBg, warningFg)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pressable(onPress = onClick)
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background2)
            .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r12),
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp12),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LogTagBadge(tag = log.tag, color = accentColor)
                    DsText(
                        text = formatLogTimestamp(log.timestamp),
                        style = Theme.typography.label01,
                        color = Theme.colors.content04,
                    )
                }

                DsText(
                    text = highlightedMessage,
                    style = Theme.typography.body02,
                    color = Theme.colors.content01,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewDefaultLogItemSingle() {
    ThemeProvider {
        DefaultLogItem(
            log = Log("Request completed successfully", tag = "HTTP", level = LogLevel.Success),
            onClick = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewDefaultLogItemMultiline() {
    ThemeProvider {
        DefaultLogItem(
            log = Log(
                message = "Exception in thread \"main\"\n" +
                    "java.lang.NullPointerException\n" +
                    "\tat com.example.Foo.bar(Foo.kt:42)",
                tag = "CRASH",
                level = LogLevel.Fatal,
            ),
            onClick = {},
        )
    }
}
