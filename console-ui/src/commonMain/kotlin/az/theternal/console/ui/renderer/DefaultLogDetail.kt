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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import az.theternal.console.core.base.Log
import az.theternal.console.ui.designsystem.DsTheme
import az.theternal.console.ui.ds.DsDivider
import az.theternal.console.ui.ds.DsIcon
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle
import az.theternal.console.ui.utils.LogTagBadge
import az.theternal.console.ui.utils.formatLogTimestampFull
import az.theternal.console.ui.utils.logAccentColor

private val AccentBarWidth = 3.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DefaultLogDetail(
    log: Log,
    onBack: () -> Unit,
) {
    val accentColor = log.logAccentColor()
    val clipboard = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (log.tag != null) {
                        LogTagBadge(tag = log.tag, color = accentColor)
                    } else {
                        DsText("Log Detail", style = DsTextStyle.TitleMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        DsIcon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { clipboard.setText(AnnotatedString(log.message)) }) {
                        DsIcon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy",
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
                .padding(DsTheme.dimens.md),
            verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.md),
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
            .clip(DsTheme.rounding.lg)
            .background(DsTheme.colors.background2)
            .border(DsTheme.metrics.borderWidth, accentColor.copy(alpha = 0.3f), DsTheme.rounding.lg),
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(AccentBarWidth)
                    .fillMaxHeight()
                    .background(accentColor),
            )
            SelectionContainer(modifier = Modifier.weight(1f)) {
                androidx.compose.material3.Text(
                    text = log.message,
                    modifier = Modifier.padding(DsTheme.dimens.md),
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = DsTheme.colors.content1,
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
            .clip(DsTheme.rounding.lg)
            .background(DsTheme.colors.background2)
            .border(DsTheme.metrics.borderWidth, DsTheme.colors.border, DsTheme.rounding.lg),
    ) {
        Column {
            MetaRow(label = "Tag") {
                if (log.tag != null) {
                    LogTagBadge(tag = log.tag, color = accentColor)
                } else {
                    DsText("—", style = DsTextStyle.Body, color = DsTheme.colors.content3)
                }
            }
            DsDivider()
            MetaRow(label = "Time") {
                DsText(
                    text = formatLogTimestampFull(log.timestamp),
                    style = DsTextStyle.Body,
                    color = DsTheme.colors.content1,
                )
            }
            DsDivider()
            MetaRow(label = "ID") {
                DsText(
                    text = log.id,
                    style = DsTextStyle.Body,
                    color = DsTheme.colors.content3,
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
            .padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.md),
    ) {
        DsText(
            text = label,
            style = DsTextStyle.LabelSmall,
            color = DsTheme.colors.content3,
            modifier = Modifier.width(40.dp),
        )
        content()
    }
}
