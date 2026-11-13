package io.thernal.console.api.addon

import androidx.compose.runtime.mutableStateListOf
import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Registry of dock widgets, the dock-wide visibility config, and commands to the dock on screen. */
object ConsoleDock {
    private const val COMMAND_BUFFER = 8

    private val _widgets = mutableStateListOf<ConsoleDockWidget>()
    val widgets: List<ConsoleDockWidget> get() = _widgets

    private val _config = MutableStateFlow(ConsoleDockConfig())
    val config: StateFlow<ConsoleDockConfig> = _config.asStateFlow()

    private val _commands = MutableSharedFlow<ConsoleDockCommand>(extraBufferCapacity = COMMAND_BUFFER)

    /** Commands for the dock on screen. Nothing keeps them: one sent while no dock is on screen is dropped. */
    val commands: SharedFlow<ConsoleDockCommand> = _commands.asSharedFlow()

    private val _layout = MutableStateFlow<ConsoleDockLayout?>(null)

    /** Where the dock on screen is, or `null` while none is (no widget shown, or the dock hidden). */
    val layout: StateFlow<ConsoleDockLayout?> = _layout.asStateFlow()

    fun updateConfig(config: ConsoleDockConfig) {
        _config.value = config
    }

    fun updateConfig(transform: ConsoleDockConfig.() -> ConsoleDockConfig) {
        _config.update { it.transform() }
    }

    /** Sends [command] to the dock on screen; see [commands]. */
    fun dispatch(command: ConsoleDockCommand) {
        _commands.tryEmit(command)
    }

    /** Called by the dock on screen whenever its layout changes, and with `null` when it leaves the screen. */
    @ConsoleInternalApi
    fun reportLayout(layout: ConsoleDockLayout?) {
        _layout.value = layout
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
