package io.thernal.console.api.addon

import androidx.compose.runtime.mutableStateListOf

object ConsoleNavigation {
    private val _tabs = mutableStateListOf<ConsoleTab>()
    private val _graphs = mutableStateListOf<ConsoleNavGraph>()

    val tabs: List<ConsoleTab> get() = _tabs
    val graphs: List<ConsoleNavGraph> get() = _graphs

    internal fun registerTab(tab: ConsoleTab) {
        if (_tabs.none { it === tab }) {
            val index = _tabs.indexOfFirst { it.order > tab.order }
            if (index == -1) {
                _tabs.add(tab)
            } else {
                _tabs.add(index, tab)
            }
        }
    }

    internal fun registerGraph(graph: ConsoleNavGraph) {
        if (_graphs.none { it === graph }) {
            _graphs.add(graph)
        }
    }
}
