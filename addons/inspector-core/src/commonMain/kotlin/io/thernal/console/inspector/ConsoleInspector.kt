package io.thernal.console.inspector

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Facade of the UI inspector addon: configuration plus one-shot commands. */
object ConsoleInspector {
    private const val COMMAND_BUFFER = 8

    private val _config = MutableStateFlow(InspectorConfig())
    val config: StateFlow<InspectorConfig> = _config.asStateFlow()

    private val _commands = MutableSharedFlow<InspectorCommand>(extraBufferCapacity = COMMAND_BUFFER)
    val commands: SharedFlow<InspectorCommand> = _commands.asSharedFlow()

    fun updateConfig(config: InspectorConfig) {
        _config.value = config.normalized()
    }

    fun updateConfig(transform: InspectorConfig.() -> InspectorConfig) {
        _config.update { it.transform().normalized() }
    }

    fun dispatch(command: InspectorCommand) {
        _commands.tryEmit(command)
    }
}
