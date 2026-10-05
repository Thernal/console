package io.thernal.console.api.addon

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Registry of dock widgets plus the dock-wide visibility config. */
object ConsoleDock {
    private val _widgets = mutableStateListOf<ConsoleDockWidget>()
    val widgets: List<ConsoleDockWidget> get() = _widgets

    private val _config = MutableStateFlow(ConsoleDockConfig())
    val config: StateFlow<ConsoleDockConfig> = _config.asStateFlow()

    fun updateConfig(config: ConsoleDockConfig) {
        _config.value = config
    }

    fun updateConfig(transform: ConsoleDockConfig.() -> ConsoleDockConfig) {
        _config.update { it.transform() }
    }

    internal fun register(widget: ConsoleDockWidget) {
        if (_widgets.any { it === widget }) return
        check(_widgets.none { it.id == widget.id }) { "A dock widget with id '${widget.id}' is already registered" }
        val index = _widgets.indexOfFirst { isBefore(widget, it) }
        if (index == -1) _widgets.add(widget) else _widgets.add(index, widget)
    }

    private fun isBefore(
        widget: ConsoleDockWidget,
        other: ConsoleDockWidget,
    ): Boolean {
        if (widget.order != other.order) return widget.order < other.order
        return widget.title.compareTo(other.title, ignoreCase = true) < 0
    }
}
