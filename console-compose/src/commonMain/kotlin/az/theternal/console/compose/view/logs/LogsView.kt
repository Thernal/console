package az.theternal.console.compose.view.logs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import az.theternal.console.api.navigation.ConsoleRoute
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.api.ui.LocalLogRenderer
import az.theternal.console.compose.ConsoleLogObserver
import az.theternal.console.runtime.LogLevel

@Composable
internal fun LogsView() {
    val navigator = LocalConsoleNavigator.current
    val logs by ConsoleLogObserver.logs.collectAsState()
    val renderer = LocalLogRenderer.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    var selectedLevels by remember { mutableStateOf(emptySet<LogLevel>()) }

    val allTags = remember(logs) {
        logs.mapNotNull { it.tag }.distinct()
    }

    val allLevels = remember {
        LogLevel.entries.filter { it != LogLevel.None }
    }

    val filteredLogs = remember(logs, searchQuery, selectedTags, selectedLevels) {
        logs.filter { log ->
            val matchesTag = selectedTags.isEmpty() || log.tag in selectedTags
            val matchesLevel = selectedLevels.isEmpty() || log.level in selectedLevels
            val matchesSearch = searchQuery.isBlank() || log.message.contains(searchQuery, ignoreCase = true)
            matchesTag && matchesLevel && matchesSearch
        }
    }

    LogsContent(
        logs = filteredLogs,
        hasAnyLogs = logs.isNotEmpty(),
        allTags = allTags,
        selectedTags = selectedTags,
        onTagToggle = { tag ->
            selectedTags = if (tag in selectedTags) selectedTags - tag else selectedTags + tag
        },
        onTagSelectAll = { selectedTags = emptySet() },
        allLevels = allLevels,
        selectedLevels = selectedLevels,
        onLevelToggle = { level ->
            selectedLevels = if (level in selectedLevels) selectedLevels - level else selectedLevels + level
        },
        onLevelSelectAll = { selectedLevels = emptySet() },
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        onLogClick = { log ->
            navigator.push(
                key = ConsoleRoute.LogDetail(
                    groupId = "",
                    logId = log.id,
                ),
            )
        },
        renderer = renderer,
    )
}
