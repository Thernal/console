package az.theternal.console.compose.view.logs.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.theternal.console.compose.ConsoleLogObserver
import az.theternal.console.compose.core.IntentHandler
import az.theternal.console.compose.core.StateHolder
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel
import kotlinx.coroutines.launch

class LogsViewModel : ViewModel(), StateHolder, IntentHandler<LogsIntent> {
    val state = LogsState()

    private var rawLogs: List<Log> = emptyList()

    init {
        viewModelScope.launch {
            ConsoleLogObserver.logs.collect { logs ->
                rawLogs = logs
                syncFromRaw()
            }
        }
    }

    override val handler = Handler { intent ->
        when (intent) {
            is LogsIntent.ToggleTag -> toggleTag(intent.tag)
            LogsIntent.SelectAllTags -> selectAllTags()
            is LogsIntent.ToggleLevel -> toggleLevel(intent.level)
            LogsIntent.SelectAllLevels -> selectAllLevels()
            is LogsIntent.SetQuery -> setQuery(intent.query)
        }
    }

    private fun syncFromRaw() {
        state.tags.update { rawLogs.mapNotNull { it.tag }.distinct() }
        state.hasAnyLogs.update { rawLogs.isNotEmpty() }
        filterLogs()
    }

    private fun filterLogs() {
        state.logs.update {
            rawLogs.filter { log ->
                val matchesTag = state.selectedTags.value.isEmpty() || log.tag in state.selectedTags.value
                val matchesLevel = state.selectedLevels.value.isEmpty() || log.level in state.selectedLevels.value
                val matchesSearch = state.searchQuery.value.text.isBlank() ||
                    log.message.contains(state.searchQuery.value.text, ignoreCase = true)
                matchesTag && matchesLevel && matchesSearch
            }.reversed()
        }
    }

    private fun toggleTag(tag: String) {
        state.selectedTags.update { if (tag in this) this - tag else this + tag }
        filterLogs()
    }

    private fun selectAllTags() {
        state.selectedTags.update { emptySet() }
        filterLogs()
    }

    private fun toggleLevel(level: LogLevel) {
        state.selectedLevels.update { if (level in this) this - level else this + level }
        filterLogs()
    }

    private fun selectAllLevels() {
        state.selectedLevels.update { emptySet() }
        filterLogs()
    }

    private fun setQuery(query: TextFieldValue) {
        state.searchQuery.update { query }
        filterLogs()
    }
}
