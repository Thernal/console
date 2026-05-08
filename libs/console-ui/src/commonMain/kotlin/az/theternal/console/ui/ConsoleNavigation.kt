package az.theternal.console.ui

import androidx.compose.runtime.mutableStateListOf
import az.theternal.console.ui.base.NavGraph
import az.theternal.console.ui.screen.logs.LogsNavGraph

object ConsoleNavigation {
    private val _graphs = mutableStateListOf<NavGraph>(LogsNavGraph)
    val graphs: List<NavGraph> get() = _graphs

    fun register(graph: NavGraph) {
        _graphs.add(graph)
    }
}
