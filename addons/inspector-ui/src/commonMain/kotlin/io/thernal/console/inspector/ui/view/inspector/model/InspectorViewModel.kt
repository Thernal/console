package io.thernal.console.inspector.ui.view.inspector.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.InspectorQueries
import io.thernal.console.inspector.ui.engine.tree.InspectorSnapshot
import io.thernal.console.inspector.ui.view.inspector.TreeRows
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.microseconds

internal class InspectorViewModel : ViewModel(), StateHolder, IntentHandler<InspectorIntent> {
    val state = InspectorState()

    private val collapsed = MutableStateFlow(emptySet<Any>())
    private val query = MutableStateFlow("")

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is InspectorIntent.SelectPage -> state.page.set(intent.page)
            is InspectorIntent.SelectNode -> selectNode(intent.key)
            InspectorIntent.SelectParent -> selectParent()
            is InspectorIntent.ToggleCollapsed -> toggleCollapsed(intent.key)
            is InspectorIntent.SetQuery -> setQuery(intent.query)
        }
    }

    init {
        InspectorEngine.refresh()
        observeRows()
        observeDetails()
        observeStats()
    }

    private fun toggleCollapsed(key: Any) {
        collapsed.update { keys -> if (key in keys) keys - key else keys + key }
    }

    private fun setQuery(value: TextFieldValue) {
        state.query.set(value)
        query.value = value.text
    }

    private fun selectNode(key: Any) {
        InspectorEngine.refresh(force = true)
        InspectorEngine.select(key)
        state.page.set(InspectorPage.Details)
    }

    private fun selectParent() {
        val snapshot = InspectorEngine.snapshot.value
        val parent = snapshot.nodes.getOrNull(InspectorQueries.selectedNode()?.parentId ?: -1) ?: return
        InspectorEngine.select(parent.key)
    }

    private fun observeRows() {
        viewModelScope.launch {
            combine(
                InspectorEngine.snapshot,
                InspectorEngine.nodeCounts,
                InspectorEngine.selectedKey,
                ConsoleInspector.config,
                combine(collapsed, query) { keys, text -> keys to text },
            ) { snapshot, counts, selectedKey, config, (collapsedKeys, text) ->
                val rows = TreeRows.build(snapshot, config.hideFrameworkNodes, collapsedKeys, text, counts, selectedKey)
                rows to summary(snapshot, rows.size)
            }.collect { (rows, summary) ->
                state.rows.set(rows)
                state.summary.set(summary)
            }
        }
    }

    private fun observeDetails() {
        viewModelScope.launch {
            combine(InspectorEngine.snapshot, InspectorEngine.selectedKey, InspectorEngine.nodeCounts) { _, _, _ ->
                InspectorQueries.selectedNode()?.let(InspectorQueries::detailsOf)
            }.collect { state.details.set(it) }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            combine(InspectorEngine.timing, InspectorEngine.nodeCounts, ConsoleInspector.config) { timing, _, config ->
                timing to InspectorQueries.ranking(RANKING_LIMIT, config.hideFrameworkNodes)
            }.collect { (timing, ranking) ->
                state.timing.set(timing)
                state.ranking.set(ranking)
            }
        }
    }

    private fun summary(
        snapshot: InspectorSnapshot,
        visibleCount: Int,
    ): String = "$visibleCount of ${snapshot.nodes.size} composables · " +
        "${snapshot.subCompositionCount} sub-compositions · " +
        "built in ${snapshot.buildMicros.microseconds.inWholeMilliseconds} ms"
}

private const val RANKING_LIMIT = 50
