package az.theternal.console.ui.renderer.defaultlogdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import az.theternal.console.model.Log
import az.theternal.console.model.LogLevel
import az.theternal.console.ui.renderer.defaultlogdetail.components.MessageCard
import az.theternal.console.ui.renderer.defaultlogdetail.components.metacard.MetaCard
import az.theternal.console.ui.utils.LogTagBadge
import az.theternal.console.ui.utils.logAccentColor
import az.theternal.console.designsystem.components.core.DsAppBar
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsIconButton
import az.theternal.console.designsystem.components.core.DsScaffold
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun DefaultLogDetail(
    log: Log,
    onBack: () -> Unit,
) {
    val accentColor = log.logAccentColor()
    val clipboard = LocalClipboardManager.current

    DsScaffold(
        topBar = {
            DsAppBar(
                leading = {
                    DsIconButton(onClick = onBack) {
                        DsIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBackIos,
                            color = Theme.colors.content02,
                        )
                    }

                    if (log.tag != null) {
                        LogTagBadge(tag = log.tag, color = accentColor)
                    } else {
                        DsText(
                            text = "Log Detail",
                            style = Theme.typography.title01,
                            color = Theme.colors.content01,
                        )
                    }
                },
                trailing = {
                    DsIconButton(
                        onClick = { clipboard.setText(AnnotatedString(log.message)) },
                    ) {
                        DsIcon(
                            icon = Icons.Outlined.ContentCopy,
                            color = Theme.colors.content02,
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
                .padding(
                    horizontal = Theme.dimens.dp12,
                    vertical = Theme.dimens.dp16,
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp12),
        ) {
            MessageCard(log = log, accentColor = accentColor)
            MetaCard(log = log, accentColor = accentColor)
        }
    }
}

@DsPreview
@Composable
private fun PreviewDefaultLogDetail() {
    ThemeProvider {
        DefaultLogDetail(
            log = Log(
                message = "Response received\nBody: {\"status\":\"ok\",\"userId\":42}",
                tag = "API",
                level = LogLevel.Info,
            ),
            onBack = {},
        )
    }
}
