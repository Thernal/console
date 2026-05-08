package az.theternal.console.ui.screen.console

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import az.theternal.console.ui.ConsoleNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConsoleScreen(onClose: () -> Unit) {
    val graphs = ConsoleNavigation.graphs
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val safeIndex = selectedIndex.coerceIn(0, (graphs.size - 1).coerceAtLeast(0))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Console") },
                actions = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                            icon = { Icon(graph.icon, contentDescription = null) },
                            label = { Text(graph.title) },
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
