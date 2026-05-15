package az.theternal.console.ui.renderer.defaultlogitem

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import az.theternal.console.ui.designsystem.foundation.theme.DsPreview
import az.theternal.console.runtime.model.Log
import az.theternal.console.runtime.model.LogLevel
import az.theternal.console.ui.designsystem.components.provider.ThemeProvider
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.renderer.defaultlogitem.components.LogItemPill
import az.theternal.console.ui.utils.LogTagBadge
import az.theternal.console.ui.utils.formatLogTimestamp
import az.theternal.console.ui.utils.logAccentColor

private val accentBarWidth = Theme.dimens.dp3

@Composable
internal fun DefaultLogItem(
    log: Log,
    onClick: () -> Unit,
) {
    val accentColor = log.logAccentColor()
    val clipboard = LocalClipboardManager.current
    val lines = remember(log.message) { log.message.lines() }
    val isMultiline = lines.size > 1
    var expanded by remember { mutableStateOf(false) }
    val chevronAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp12)
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background2)
            .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r12)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            // Accent bar
            Box(
                modifier = Modifier
                    .width(accentBarWidth)
                    .fillMaxHeight()
                    .background(accentColor),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Theme.dimens.dp10, vertical = Theme.dimens.dp10),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
            ) {
                // Header row: tag badge + timestamp
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

                // First line of message — always visible
                DsText(
                    text = lines.first(),
                    style = Theme.typography.body02,
                    color = Theme.colors.content01,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Multi-line controls
                if (isMultiline) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LogItemPill(
                            icon = Icons.Outlined.ExpandMore,
                            label = if (expanded) {
                                "Collapse"
                            } else {
                                val extra = lines.size - 1
                                "$extra more line${if (extra > 1) "s" else ""}"
                            },
                            iconModifier = Modifier.rotate(chevronAngle),
                            onClick = { expanded = !expanded },
                        )
                        LogItemPill(
                            icon = Icons.Outlined.ContentCopy,
                            label = "Copy",
                            onClick = { clipboard.setText(AnnotatedString(log.message)) },
                        )
                    }

                    if (expanded) {
                        SelectionContainer {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(Theme.rounding.r8)
                                    .background(Theme.colors.background3)
                                    .border(
                                        width = Theme.metrics.borderWidth,
                                        color = Theme.colors.border,
                                        shape = Theme.rounding.r8,
                                    )
                                    .padding(Theme.dimens.dp10),
                            ) {
                                DsText(
                                    text = log.message,
                                    style = Theme.typography.body03.copy(
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = Theme.typography.body02.lineHeight,
                                    ),
                                    color = Theme.colors.content02,
                                )
                            }
                        }
                    }
                }
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
