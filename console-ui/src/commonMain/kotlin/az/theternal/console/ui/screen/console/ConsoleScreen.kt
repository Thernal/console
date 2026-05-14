package az.theternal.console.ui.screen.console

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
import az.theternal.console.ui.designsystem.components.core.DsNavigationBar
import az.theternal.console.ui.designsystem.components.core.DsNavigationBarItem
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
                title = { DsText("Console", style = Theme.typography.title01) },
                actions = {
                    DsIconButton(onClick = onClose) {
                        DsIcon(Icons.Default.Close)
                    }
                },
            )
        },
        bottomBar = if (graphs.size > 1) {
            {
                DsNavigationBar {
                    graphs.forEachIndexed { index, graph ->
                        DsNavigationBarItem(
                            selected = index == safeIndex,
                            onClick = { selectedIndex = index },
                            icon = { DsIcon(graph.icon) },
                            label = {
                                DsText(
                                    graph.title,
                                    style = Theme.typography.label01,
                                    color = if (index == safeIndex) {
                                        Theme.colors.primary01
                                    } else {
                                        Theme.colors.content03
                                    },
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
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            graphs.getOrNull(safeIndex)?.Content()
        }
    }
}
