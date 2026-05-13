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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import az.theternal.console.runtime.model.Log
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.utils.LogTagBadge
import az.theternal.console.ui.utils.formatLogTimestamp
import az.theternal.console.ui.utils.logAccentColor

private val AccentBarWidth = Theme.dimens.dp3

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
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp4)
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background2)
            .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r12)
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
                    .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LogTagBadge(tag = log.tag, color = accentColor)
                    DsText(
                        text = formatLogTimestamp(log.timestamp),
                        style = Theme.typography.label02,
                        color = Theme.colors.content04,
                    )
                }

                DsText(
                    text = lines.first(),
                    style = Theme.typography.body02,
                    color = Theme.colors.content01,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (isMultiline) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4)) {
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
                                    .clip(Theme.rounding.r8)
                                    .background(Theme.colors.background3)
                                    .padding(Theme.dimens.dp8),
                            ) {
                                DsText(
                                    text = log.message,
                                    style = Theme.typography.label01.copy(
                                        fontFamily = FontFamily.Monospace,
                                        color = Theme.colors.content02,
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
            .clip(Theme.rounding.r6)
            .background(Theme.colors.background3)
            .clickable(onClick = onClick)
            .padding(horizontal = Theme.dimens.dp8, vertical = Theme.dimens.dp2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
    ) {
        DsIcon(
            icon = icon,
            size = Theme.metrics.iconXs,
            tint = Theme.colors.content03,
            modifier = iconModifier,
        )
        DsText(text = label, style = Theme.typography.label02, color = Theme.colors.content03)
    }
}
