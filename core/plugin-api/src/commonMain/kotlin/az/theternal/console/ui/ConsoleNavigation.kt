package az.theternal.console.ui

import androidx.compose.runtime.mutableStateListOf
import az.theternal.console.ui.base.NavGraph

object ConsoleNavigation {
    private val _graphs = mutableStateListOf<NavGraph>()
    val graphs: List<NavGraph> get() = _graphs

    fun register(graph: NavGraph) {
        if (_graphs.none { it::class == graph::class }) {
            val index = _graphs.indexOfFirst { it.order > graph.order }
            if (index == -1) _graphs.add(graph) else _graphs.add(index, graph)
        }
    }
}
