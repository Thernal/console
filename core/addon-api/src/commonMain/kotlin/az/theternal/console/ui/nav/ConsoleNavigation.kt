package az.theternal.console.ui.nav

import androidx.compose.runtime.mutableStateListOf

object ConsoleNavigation {
    private val _graphs = mutableStateListOf<ConsoleNavGraph>()
    val graphs: List<ConsoleNavGraph> get() = _graphs
    val tabs: List<ConsoleTab> get() = _graphs.filterIsInstance<ConsoleTab>()

    fun register(graph: ConsoleNavGraph) {
        if (_graphs.none { it::class == graph::class }) {
            val order = (graph as? ConsoleTab)?.order ?: Int.MAX_VALUE
            val index = _graphs.indexOfFirst { (it as? ConsoleTab)?.order ?: Int.MAX_VALUE > order }
            if (index == -1) _graphs.add(graph) else _graphs.add(index, graph)
        }
    }
}
