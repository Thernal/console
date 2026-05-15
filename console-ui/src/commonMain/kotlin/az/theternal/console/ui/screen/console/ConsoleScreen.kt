package az.theternal.console.ui.screen.console

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import az.theternal.console.ui.nav.ConsoleNavigation
import az.theternal.console.ui.nav.ConsoleTab
import az.theternal.console.ui.designsystem.components.core.DsIconButton
import az.theternal.console.ui.designsystem.components.core.navigationbar.DsNavigationBar
import az.theternal.console.ui.designsystem.components.core.navigationbar.components.DsNavigationBarItem
import az.theternal.console.ui.designsystem.components.core.DsScaffold
import az.theternal.console.ui.designsystem.components.core.DsTopAppBar
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsText

@Composable
internal fun ConsoleScreen(
    onClose: () -> Unit,
    requestedTab: ConsoleTab? = null,
    onRequestConsumed: () -> Unit = {},
) {
    val graphs = ConsoleNavigation.tabs
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val safeIndex = selectedIndex.coerceIn(0, (graphs.size - 1).coerceAtLeast(0))

    LaunchedEffect(requestedTab) {
        if (requestedTab != null) {
            val idx = graphs.indexOf(requestedTab)
            if (idx >= 0) selectedIndex = idx
            onRequestConsumed()
        }
    }

    DsScaffold(
        topBar = {
            DsTopAppBar(
                title = {
                    DsText(
                        text = "Console",
                        style = Theme.typography.title01,
                        color = Theme.colors.content01,
                    )
                },
                actions = {
                    DsIconButton(onClick = onClose) {
                        DsIcon(
                            icon = Icons.Default.Close,
                            tint = Theme.colors.content02,
                        )
                    }
                },
            )
        },
        bottomBar = if (graphs.size > 1) {
            {
                DsNavigationBar {
                    graphs.forEachIndexed { index, graph ->
                        val isSelected = index == safeIndex
                        DsNavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedIndex = index },
                            icon = {
                                DsIcon(
                                    icon = graph.icon,
                                    size = Theme.metrics.iconMd,
                                )
                            },
                            label = {
                                DsText(
                                    text = graph.title,
                                    style = Theme.typography.label01,
                                )
                            },
                        )
                    }
                }
            }
        } else {
            null
        },
    ) { padding ->
        AnimatedContent(
            targetState = safeIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "console_tab",
        ) { index ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                graphs.getOrNull(index)?.Content()
            }
        }
    }
}
