package io.thernal.console.compose.view.defaultdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.compose.core.select
import io.thernal.console.compose.util.LocalSearchQuery
import io.thernal.console.compose.util.logAccentColor
import io.thernal.console.compose.view.defaultdetail.components.MessageCard
import io.thernal.console.compose.view.defaultdetail.components.metacard.MetaCard
import io.thernal.console.compose.view.defaultdetail.model.DefaultLogDetailIntent
import io.thernal.console.compose.view.defaultdetail.model.DefaultLogDetailState
import io.thernal.console.designsystem.components.core.DsAppBar
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsScaffold
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.runtime.Log
import io.thernal.console.runtime.LogLevel

@Composable
internal fun DefaultLogDetailContent(
    log: Log,
    onBack: () -> Unit,
    state: DefaultLogDetailState,
    onDispatch: (DefaultLogDetailIntent) -> Unit,
) {
    val accentColor = log.logAccentColor()

    CompositionLocalProvider(LocalSearchQuery provides state.detailQuery.select { it.text }) {
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
                            onClick = { onDispatch(DefaultLogDetailIntent.CopyMessage) },
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
                    value = state.detailQuery.value,
                    onValueChange = { onDispatch(DefaultLogDetailIntent.SetQuery(it)) },
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
                        if (state.detailQuery.value.text.isNotEmpty()) {
                            DsIconButton(
                                onClick = { onDispatch(DefaultLogDetailIntent.SetQuery(TextFieldValue())) },
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
private fun PreviewDefaultLogDetailContent() {
    ThemeProvider {
        DefaultLogDetailContent(
            log = Log(
                message = "Response received\nBody: {\"status\":\"ok\",\"userId\":42}",
                tag = "API",
                level = LogLevel.Info,
            ),
            onBack = {},
            state = DefaultLogDetailState(),
            onDispatch = {},
        )
    }
}
