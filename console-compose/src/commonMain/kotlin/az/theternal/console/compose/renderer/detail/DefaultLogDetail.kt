package az.theternal.console.compose.renderer.detail

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import az.theternal.console.compose.util.toTextClipEntry
import kotlinx.coroutines.launch
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel
import az.theternal.console.compose.renderer.detail.components.MessageCard
import az.theternal.console.compose.renderer.detail.components.metacard.MetaCard
import az.theternal.console.compose.util.logAccentColor
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
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

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

                    DsText(
                        text = "Log Detail",
                        style = Theme.typography.title01,
                        color = Theme.colors.content01,
                    )
                },
                trailing = {
                    DsIconButton(
                        onClick = { scope.launch { clipboard.setClipEntry(log.message.toTextClipEntry()) } },
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
