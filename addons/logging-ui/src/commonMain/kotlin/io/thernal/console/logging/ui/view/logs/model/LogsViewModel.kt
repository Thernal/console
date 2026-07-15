package io.thernal.console.logging.ui.view.logs.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.logging.ui.ConsoleLogObserver
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlinx.coroutines.launch

class LogsViewModel : ViewModel(), StateHolder, IntentHandler<LogsIntent> {
    val state = LogsState()

    private var rawLogs: List<Log> = emptyList()

    /**
     * Append-only: a tag's position is fixed the first time it's ever seen and never
     * recomputed from the (evicting) ring buffer, so a tag doesn't reorder just because its
     * oldest surviving log got dropped past `maxLogCount` — only whether it's currently shown
     * (see [syncFromRaw]) depends on the live buffer.
     */
    private val tagFirstSeenOrder = mutableListOf<String>()

    init {
        viewModelScope.launch {
            ConsoleLogObserver.logs.collect { logs ->
                rawLogs = logs
                syncFromRaw()
            }
        }
    }

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is LogsIntent.ToggleTag -> toggleTag(intent.tag)
            LogsIntent.SelectAllTags -> selectAllTags()
            is LogsIntent.ToggleLevel -> toggleLevel(intent.level)
            LogsIntent.SelectAllLevels -> selectAllLevels()
            is LogsIntent.SetQuery -> setQuery(intent.query)
        }
    }

    private fun syncFromRaw() {
        val currentTags = rawLogs.mapNotNullTo(mutableSetOf()) { it.tag }
        currentTags.forEach { tag -> if (tag !in tagFirstSeenOrder) tagFirstSeenOrder += tag }
        state.tags.update { tagFirstSeenOrder.filter { it in currentTags } }
        state.hasAnyLogs.update { rawLogs.isNotEmpty() }
        filterLogs()
    }

    private fun filterLogs() {
        val tags = state.selectedTags.value
        val levels = state.selectedLevels.value
        val query = state.searchQuery.value.text
        state.logs.set(
            rawLogs.filter { log ->
                (tags.isEmpty() || log.tag in tags) &&
                    (levels.isEmpty() || log.level in levels) &&
                    (query.isBlank() || log.message.contains(query, ignoreCase = true))
            }.reversed(),
        )
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
