package az.theternal.console.ui.screen.console

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import az.theternal.console.ui.ConsoleNavigation
import az.theternal.console.ui.designsystem.DsTheme
import az.theternal.console.ui.ds.DsIcon
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConsoleScreen(
    onClose: () -> Unit,
    requestedTabTitle: String? = null,
    onRequestConsumed: () -> Unit = {},
) {
    val graphs = ConsoleNavigation.graphs
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val safeIndex = selectedIndex.coerceIn(0, (graphs.size - 1).coerceAtLeast(0))

    LaunchedEffect(requestedTabTitle) {
        if (requestedTabTitle != null) {
            val idx = graphs.indexOfFirst { it.title == requestedTabTitle }
            if (idx >= 0) selectedIndex = idx
            onRequestConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { DsText("Console", style = DsTextStyle.TitleMedium) },
                actions = {
                    IconButton(onClick = onClose) {
                        DsIcon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
            )
        },
        bottomBar = {
            if (graphs.size > 1) {
                NavigationBar {
                    graphs.forEachIndexed { index, graph ->
                        NavigationBarItem(
                            selected = index == safeIndex,
                            onClick = { selectedIndex = index },
                            icon = { DsIcon(graph.icon, contentDescription = null) },
                            label = {
                                DsText(
                                    graph.title,
                                    style = DsTextStyle.LabelMedium,
                                    color = if (index == safeIndex) {
                                        DsTheme.colors.primary
                                    } else {
                                        DsTheme.colors.content3
                                    },
                                )
                            },
                        )
                    }
                }
            }
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
