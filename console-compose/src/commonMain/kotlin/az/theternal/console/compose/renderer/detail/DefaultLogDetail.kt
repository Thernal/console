package az.theternal.console.compose.renderer.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import az.theternal.console.compose.core.select
import az.theternal.console.compose.util.LocalSearchQuery
import az.theternal.console.compose.util.toTextClipEntry
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel
import az.theternal.console.compose.renderer.detail.components.MessageCard
import az.theternal.console.compose.renderer.detail.components.metacard.MetaCard
import az.theternal.console.compose.util.logAccentColor
import az.theternal.console.designsystem.components.core.DsAppBar
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsIconButton
import az.theternal.console.designsystem.components.core.DsTextField
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
    val viewModel = viewModel { DefaultLogDetailViewModel() }
    val accentColor = log.logAccentColor()
    val clipboard = LocalClipboard.current

    LaunchedEffect(viewModel.state.copyTrigger.value) {
        if (viewModel.state.copyTrigger.value > 0) clipboard.setClipEntry(log.message.toTextClipEntry())
    }

    CompositionLocalProvider(LocalSearchQuery provides viewModel.state.detailQuery.select { it.text }) {
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
                            onClick = { viewModel.dispatch(DefaultLogDetailIntent.CopyMessage) },
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
                MetaCard(log = log, accentColor = accentColor)

                DsTextField(
                    value = viewModel.state.detailQuery.value,
                    onValueChange = { viewModel.dispatch(DefaultLogDetailIntent.SetQuery(it)) },
                    hint = "Search in message…",
                    prefix = {
                        DsIcon(
                            icon = Icons.Outlined.Search,
                            size = Theme.metrics.iconMd,
                            color = Theme.colors.content04,
                            modifier = Modifier.padding(end = Theme.dimens.dp8),
                        )
                    },
                    suffix = {
                        if (viewModel.state.detailQuery.value.text.isNotEmpty()) {
                            DsIconButton(
                                onClick = { viewModel.dispatch(DefaultLogDetailIntent.SetQuery(TextFieldValue())) },
                                contentColor = Theme.colors.content04,
                            ) {
                                DsIcon(
                                    icon = Icons.Outlined.Clear,
                                    size = Theme.metrics.iconMd,
                                )
                            }
                        }
                    },
                )

                MessageCard(log = log, accentColor = accentColor)
            }
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
