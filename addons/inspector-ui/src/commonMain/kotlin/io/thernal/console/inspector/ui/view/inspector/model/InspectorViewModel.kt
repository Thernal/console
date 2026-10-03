package io.thernal.console.inspector.ui.view.inspector.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.InspectorQueries
import io.thernal.console.inspector.ui.engine.InspectorSnapshot
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private fun setQuery(value: String) {
        state.query.set(value)
        query.value = value
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
                combine(collapsed, query) { c, q -> c to q },
            ) { snapshot, counts, selectedKey, config, (collapsedKeys, text) ->
                val rows = TreeRows.build(snapshot, config.hideFrameworkNodes, collapsedKeys, text, counts, selectedKey)
                state.summary.set(summary(snapshot, rows.size))
                rows
            }.collect { state.rows.set(it) }
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
            combine(
                InspectorEngine.timing,
                InspectorEngine.nodeCounts,
                InspectorEngine.isFrozen,
                ConsoleInspector.config,
            ) { timing, _, isFrozen, config ->
                state.timing.set(timing)
                state.isFrozen.set(isFrozen)
                state.ranking.set(InspectorQueries.ranking(RANKING_LIMIT, config.hideFrameworkNodes))
            }.collect { }
        }
    }

    private fun summary(
        snapshot: InspectorSnapshot,
        visibleCount: Int,
    ): String = "$visibleCount of ${snapshot.nodes.size} composables · " +
        "${snapshot.subCompositionCount} sub-compositions · built in ${snapshot.buildMicros / MICROS_PER_MILLI} ms"

    private companion object {
        const val RANKING_LIMIT = 50
        const val MICROS_PER_MILLI = 1_000L
    }
}
