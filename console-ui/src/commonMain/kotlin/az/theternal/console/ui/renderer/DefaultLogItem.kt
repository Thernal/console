package az.theternal.console.ui.renderer

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import az.theternal.console.core.base.Log
import az.theternal.console.ui.designsystem.DsTheme
import az.theternal.console.ui.ds.DsIcon
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle
import az.theternal.console.ui.utils.LogTagBadge
import az.theternal.console.ui.utils.formatLogTimestamp
import az.theternal.console.ui.utils.logAccentColor

private val AccentBarWidth = 3.dp

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.xs)
            .clip(DsTheme.rounding.lg)
            .background(DsTheme.colors.background2)
            .border(DsTheme.metrics.borderWidth, DsTheme.colors.border, DsTheme.rounding.lg)
            .clickable(onClick = onClick)
            .animateContentSize(),
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(AccentBarWidth)
                    .fillMaxHeight()
                    .background(accentColor),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.sm),
                verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.sm),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LogTagBadge(tag = log.tag, color = accentColor)
                    DsText(
                        text = formatLogTimestamp(log.timestamp),
                        style = DsTextStyle.LabelSmall,
                        color = DsTheme.colors.content4,
                    )
                }

                DsText(
                    text = lines.first(),
                    style = DsTextStyle.Body,
                    color = DsTheme.colors.content1,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (isMultiline) {
                    Row(horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xs)) {
                        LogItemPill(
                            icon = Icons.Outlined.ExpandMore,
                            label = if (expanded) "Collapse" else "${lines.size - 1} more",
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
                                    .clip(DsTheme.rounding.md)
                                    .background(DsTheme.colors.background3)
                                    .padding(DsTheme.dimens.sm),
                            ) {
                                androidx.compose.material3.Text(
                                    text = log.message,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 17.sp,
                                        color = DsTheme.colors.content2,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LogItemPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconModifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clip(DsTheme.rounding.sm)
            .background(DsTheme.colors.background3)
            .clickable(onClick = onClick)
            .padding(horizontal = DsTheme.dimens.sm, vertical = DsTheme.dimens.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xxs),
    ) {
        DsIcon(
            imageVector = icon,
            contentDescription = null,
            tint = DsTheme.colors.content3,
            modifier = Modifier.size(DsTheme.metrics.iconXs).then(iconModifier),
        )
        DsText(text = label, style = DsTextStyle.LabelSmall, color = DsTheme.colors.content3)
    }
}
