package az.theternal.console.ui.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import az.theternal.console.core.base.Log
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsIconButton
import az.theternal.console.ui.designsystem.components.core.DsScaffold
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.components.core.DsTopAppBar
import az.theternal.console.ui.utils.LogTagBadge
import az.theternal.console.ui.utils.formatLogTimestampFull
import az.theternal.console.ui.utils.logAccentColor

private val AccentBarWidth = Theme.dimens.dp3

@Composable
internal fun DefaultLogDetail(
    log: Log,
    onBack: () -> Unit,
) {
    val accentColor = log.logAccentColor()
    val clipboard = LocalClipboardManager.current

    DsScaffold(
        topBar = {
            DsTopAppBar(
                title = {
                    if (log.tag != null) {
                        LogTagBadge(tag = log.tag, color = accentColor)
                    } else {
                        DsText("Log Detail", style = Theme.typography.title01)
                    }
                },
                navigationIcon = {
                    DsIconButton(onClick = onBack) {
                        DsIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                        )
                    }
                },
                actions = {
                    DsIconButton(onClick = { clipboard.setText(AnnotatedString(log.message)) }) {
                        DsIcon(
                            icon = Icons.Outlined.ContentCopy,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Theme.dimens.dp12),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp12),
        ) {
            MessageCard(log = log, accentColor = accentColor)
            MetaCard(log = log, accentColor = accentColor)
        }
    }
}

@Composable
private fun MessageCard(
    log: Log,
    accentColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background2)
            .border(Theme.metrics.borderWidth, accentColor.copy(alpha = 0.3f), Theme.rounding.r12),
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(AccentBarWidth)
                    .fillMaxHeight()
                    .background(accentColor),
            )
            SelectionContainer(modifier = Modifier.weight(1f)) {
                DsText(
                    text = log.message,
                    modifier = Modifier.padding(Theme.dimens.dp12),
                    style = Theme.typography.body02.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = Theme.typography.body01.lineHeight,
                        color = Theme.colors.content01,
                    ),
                )
            }
        }
    }
}

@Composable
private fun MetaCard(
    log: Log,
    accentColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background2)
            .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r12),
    ) {
        Column {
            MetaRow(label = "Tag") {
                if (log.tag != null) {
                    LogTagBadge(tag = log.tag, color = accentColor)
                } else {
                    DsText("—", style = Theme.typography.body02, color = Theme.colors.content03)
                }
            }
            DsDivider()
            MetaRow(label = "Time") {
                DsText(
                    text = formatLogTimestampFull(log.timestamp),
                    style = Theme.typography.body02,
                    color = Theme.colors.content01,
                )
            }
            DsDivider()
            MetaRow(label = "ID") {
                DsText(
                    text = log.id,
                    style = Theme.typography.body02,
                    color = Theme.colors.content03,
                )
            }
        }
    }
}

@Composable
private fun MetaRow(
    label: String,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp12),
    ) {
        DsText(
            text = label,
            style = Theme.typography.label02,
            color = Theme.colors.content03,
            modifier = Modifier.width(Theme.dimens.dp40),
        )
        content()
    }
}
